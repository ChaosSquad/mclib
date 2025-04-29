plugins {
    id("java")
}

allprojects {
    group = "net.chaossquad"
    version = "1.0-SNAPSHOT"

    val paperVersion by extra("1.21.4-R0.1-SNAPSHOT")

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

subprojects {}