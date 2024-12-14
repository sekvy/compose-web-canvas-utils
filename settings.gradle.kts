enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "compose-web-canvas-utils"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

includeBuild("convention-plugins")
