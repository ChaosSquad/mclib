plugins {
    `kotlin-dsl`
}

group = "net.chaossquad.conventions"
version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    implementation("io.papermc.paperweight:paperweight-userdev:2.0.0-beta.8")
}