import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    val offlineMavenOnly = providers.gradleProperty("offlineMavenOnly").isPresent

    repositories {
        maven { url = uri("offline-maven") }
        if (!offlineMavenOnly) {
            gradlePluginPortal()
            mavenCentral()
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (offlineMavenOnly) {
                when (requested.id.id) {
                    "org.jetbrains.kotlin.jvm" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
                    "org.jetbrains.kotlin.plugin.spring" -> useModule("org.jetbrains.kotlin:kotlin-allopen:2.3.21")
                    "org.springframework.boot" -> useModule("org.springframework.boot:spring-boot-gradle-plugin:4.1.1")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    val offlineMavenOnly = providers.gradleProperty("offlineMavenOnly").isPresent

    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven { url = uri("offline-maven") }
        if (!offlineMavenOnly) {
            mavenCentral()
        }
    }
}

rootProject.name = "server9091"
