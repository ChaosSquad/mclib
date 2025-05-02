plugins {
    id("java")
    id("net.chaossquad.conventions.paper-plugin")
    `maven-publish`
}

group = "net.chaossquad"
version = "1.0-SNAPSHOT"

val paperVersion: String = "1.21.4-R0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "respark-releases"
        url = uri("https://maven.respark.dev/releases")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.extendedclip.com/releases/")
    }
}

dependencies {
    compileOnly("org.json:json:20240303")
    compileOnly("me.leoko.advancedgui:AdvancedGUI:2.2.9")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

java {
    withSourcesJar()
    withJavadocJar()
}

// gradle publish{PUBLICATION_NAME}To{REPOSITORY_NAME}Repository
// in this case: publishMavenToChaosSquadRepository
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "chaossquad"
            url = uri(if (version.toString().endsWith("RELEASE")) {
                "https://maven.chaossquad.net/releases"
            } else {
                "https://maven.chaossquad.net/snapshots"
            })

            credentials {
                username = findProperty("chaossquad-repository.username") as String?
                password = findProperty("chaossquad-repository.password") as String?
            }
        }
    }
}
