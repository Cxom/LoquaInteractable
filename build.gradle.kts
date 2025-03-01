plugins {
    kotlin("jvm") version "2.1.0"
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"

    id("com.gradleup.shadow") version "8.3.5"
}

group = "net.punchtree"
version = "0.0.1-SNAPSHOT"
description = "LoquaInteractable"

java.sourceCompatibility = JavaVersion.VERSION_21

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
    	url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        name = "citizens-repo"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
    maven {
        name = "codemc-repo"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

val ftpAntTask by configurations.creating

dependencies {
    compileOnly(kotlin("stdlib"))

    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    compileOnly("net.punchtree:persistentmetadata:0.0.1-SNAPSHOT")
    compileOnly("net.punchtree:punchtree-util:1.7.0-SNAPSHOT")
    
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")

    compileOnly("net.luckperms:api:5.4")

    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")

//    implementation("cloud.commandframework:cloud-paper:1.6.1")
    compileOnly("net.citizensnpcs:citizens-main:2.0.37-SNAPSHOT") {
        exclude("*", "*")
    }
    compileOnly("me.zombie_striker:QualityArmory:2.0.17")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")

    ftpAntTask("org.apache.ant:ant-commons-net:1.10.12") {
        module("commons-net:commons-net:1.4.1") {
            dependencies("oro:oro:2.0.8:jar")
        }
    }
}

kotlin {
    jvmToolchain(21)
}


tasks { 

	build {
		dependsOn(shadowJar)
	}

	compileJava {
	    options.encoding = Charsets.UTF_8.name()
	    options.release.set(21)
	}

	processResources {
		filteringCharset = Charsets.UTF_8.name()
	}

}


val ftpHostUrl: String by project
val ftpUsername: String by project
val ftpPassword: String by project
val localOutputDir: String? by project

task("uploadToServer") {
    doLast{
        ant.withGroovyBuilder {
            "taskdef"("name" to "ftp", "classname" to "org.apache.tools.ant.taskdefs.optional.net.FTP", "classpath" to ftpAntTask.asPath)
            "ftp"("server" to ftpHostUrl, "userid" to ftpUsername, "password" to ftpPassword, "remoteDir" to "/plugins") {
                "fileset"("dir" to "build/libs") {
                    "include"("name" to rootProject.name + "-" + version + ".jar")
                }
            }
        }
    }
}

val buildLocal by tasks.registering(Copy::class) {
    group = "build"
    description = "Builds the shaded JAR locally without publishing to the live server."

    from(tasks.named("shadowJar"))
    into(provider {
        if (localOutputDir?.isNotEmpty() == true) {
            localOutputDir?.let { project.file(it) }
        } else {
            logger.warn("Environment variable LOCAL_OUTPUT_DIR is not set. Using the default output directory.")
            project.file("build/libs")
        }
    })
    dependsOn("shadowJar")
}

task("buildAndUpload") {
    dependsOn("build")
    dependsOn("uploadToServer")
    tasks.findByName("uploadToServer")!!.mustRunAfter("build")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}