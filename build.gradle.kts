import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"

    id("com.github.johnrengelman.shadow") version "5.1.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

version = File("version").readText().trim()

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes(Pair("Main-Class", "hackee12.strukt.cmd.MainKt"))
    }
}