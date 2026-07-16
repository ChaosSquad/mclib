allprojects {
    group = "net.chaossquad"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://repo.extendedclip.com/releases/")
        }
        maven {
            name = "chaossquad-releases"
            url = uri("https://maven.chaossquad.net/releases")
        }
        maven {
            name = "chaossquad-snapshots"
            url = uri("https://maven.chaossquad.net/snapshots")
        }
    }
}

extra["javaVersion"] = "25"

extra["paperVersion"] = "26.2.build.+"
extra["luckPermsVersion"] = "5.5"
extra["vaultAPIVersion"] = "1.7.1"
extra["placeholderAPIVersion"] = "2.12.3"
extra["dataStorageVersion"] = "1.0-RELEASE"
