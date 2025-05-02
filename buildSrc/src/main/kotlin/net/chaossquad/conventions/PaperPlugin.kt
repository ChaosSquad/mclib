package net.chaossquad.conventions

import io.papermc.paperweight.userdev.PaperweightUserExtension
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class PaperPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.apply("java")
            plugins.apply("io.papermc.paperweight.userdev")

            group = "net.chaossquad"
            version = "1.0-SNAPSHOT"

            val paperVersion = "1.21.4-R0.1-SNAPSHOT"

            repositories {
                mavenLocal()
                mavenCentral()
                maven("https://repo.papermc.io/repository/maven-public/") {
                    name = "papermc"
                }
            }

            project.extensions.configure<PaperweightUserExtension> {
                reobfArtifactConfiguration.set(
                    ReobfArtifactConfiguration.MOJANG_PRODUCTION
                )
            }

            dependencies {
                add("paperweightDevelopmentBundle", "io.papermc.paper:dev-bundle:1.21.4-R0.1-SNAPSHOT")
            }
        }
    }
}
