plugins {
    id("java")
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

tasks.register<Jar>("protoJar") {
    archiveClassifier.set("proto")
    from("src/generated/main/java")
}