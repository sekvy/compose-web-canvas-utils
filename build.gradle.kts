import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("convention.publication")
    alias(libs.plugins.benManesVersions)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.dokka)
}

group = "se.sekvy"
version = "0.1.1-alpha"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR){browser()}
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs{browser()}

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        // Sharing kotlinx.browser etc seems broken..
        val wasmJsMain by getting {
        }
        val jsMain by getting {
        }
    }
}