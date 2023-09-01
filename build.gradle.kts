plugins {
    `java-library`
    `maven-publish`
    eclipse
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "net.punchtree"
version = "0.0.1-SNAPSHOT"
description = "LoquaInteractable"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    	url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

val ftpAntTask by configurations.creating

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

    compileOnly("net.punchtree:persistentmetadata:0.0.1-SNAPSHOT")
    compileOnly("net.punchtree:punchtree-util:0.0.1-SNAPSHOT")
    
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
//    implementation("cloud.commandframework:cloud-paper:1.6.1")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")

    ftpAntTask("org.apache.ant:ant-commons-net:1.10.12") {
        module("commons-net:commons-net:1.4.1") {
            dependencies("oro:oro:2.0.8:jar")
        }
    }
} 

tasks { 
	
	build {
		dependsOn(reobfJar)
	}
	
	compileJava {
	    options.encoding = Charsets.UTF_8.name()
	    options.release.set(17)
	}

	processResources {
		filteringCharset = Charsets.UTF_8.name()
	}

}

val ftpHostUrl: String by project
val ftpUsername: String by project
val ftpPassword: String by project
val localOutputDir: String by project

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

task("buildAndPublish") {
    dependsOn("build")
    dependsOn("uploadToServer")
    tasks.findByName("uploadToServer")!!.mustRunAfter("build")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}