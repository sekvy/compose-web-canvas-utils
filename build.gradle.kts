plugins {
    alias(deps.plugins.benManesVersions)
    alias(deps.plugins.kotlinter)
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "se.sekvy"
version = "0.1.0-alpha"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser {}
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
            }
        }
    }
}