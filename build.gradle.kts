import java.net.URI
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

group = "com.gxf.oslp-protobuf"

version = System.getenv("GITHUB_REF_NAME")
    ?.replace("/", "-")
    ?.lowercase()
    ?.let { "${it}-SNAPSHOT" }
    ?: "develop"

plugins {
    id("java")
    `maven-publish`
    alias(libs.plugins.protobuf)
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
