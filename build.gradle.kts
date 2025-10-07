import java.net.URI
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

group = "org.lfenergy.gxf.oslp-protobuf"

val semVerRegex = Regex("""^v?(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:[-+][\w\.-]+)?$""")

version = System.getenv("GITHUB_REF_NAME")
    ?.replace("/", "-")
    ?.lowercase()
    ?.let { if(semVerRegex.matches(it)) it.removePrefix("v") else "${it}-SNAPSHOT" }
    ?: "develop"

plugins {
    id("java")
    `maven-publish`
    alias(libs.plugins.protobuf)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.protoJava)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }
}

sourceSets {
    named("main") {
        java {
            srcDir("src/generated/main/java")
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        mavenLocal()
        maven {
            name = "GXFGithubPackages"
            url = URI("https://maven.pkg.github.com/osgp/oslp-protobuf")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("java") {
            from(components.getByName("java"))
        }
    }
}

tasks.register<Jar>("protoJar") {
    archiveClassifier.set("proto")
    from("src/generated/main/java")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}