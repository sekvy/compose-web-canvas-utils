
rootProject.name = "compose-web-canvas-utils"

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("./gradle/deps.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

includeBuild("convention-plugins")
