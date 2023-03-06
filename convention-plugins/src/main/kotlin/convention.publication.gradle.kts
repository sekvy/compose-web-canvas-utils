import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.util.*

plugins {
    `maven-publish`
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null
ext["info.project.url"] = null
ext["info.project.name"] = null
ext["info.project.description"] = null
ext["info.developer.id"] = null
ext["info.developer.name"] = null
ext["info.developer.email"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
private val secretPropsFile: File = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    ext["info.project.url"] = System.getenv("INFO_PROJECT_URL")
    ext["info.project.name"] = System.getenv("INFO_PROJECT_NAME")
    ext["info.project.description"] = System.getenv("INFO_PROJECT_DESCRIPTION")
    ext["info.developer.id"] = System.getenv("INFO_DEVELOPER_ID")
    ext["info.developer.name"] = System.getenv("INFO_DEVELOPER_NAME")
    ext["info.developer.email"] = System.getenv("INFO_DEVELOPER_EMAIL")
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    description = "Package all dokka HTML docs into a single jar for publication"
    dependsOn("dokkaHtml")
    from(buildDir.resolve("dokka/html"))
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {

        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set(getExtraString("info.project.name"))
            description.set(getExtraString("info.project.description"))
            url.set(getExtraString("info.project.url"))

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set(getExtraString("info.developer.id"))
                    name.set(getExtraString("info.developer.name"))
                    email.set(getExtraString("info.developer.email"))
                }
            }
            scm {
                url.set(getExtraString("info.project.url"))
            }
        }
    }
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    if (project.ext.get("signing.keyId") != null) {
        sign(publishing.publications)
    }
}
