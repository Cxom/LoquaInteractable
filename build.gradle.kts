plugins {
    `java-library`
    `maven-publish`
    eclipse
    id("io.papermc.paperweight.userdev") version "1.3.3"
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

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.18.1-R0.1-SNAPSHOT")
    
    implementation("net.punchtree:persistentmetadata:0.0.1-SNAPSHOT")
    implementation("net.punchtree:punchtree-util:0.0.1-SNAPSHOT")
    
    implementation("com.comphenix.protocol:ProtocolLib:4.7.0")
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("org.mockito:mockito-inline:3.12.4")
    //testImplementation("io.papermc.paper:paper:1.18.1-R0.1-SNAPSHOT")
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