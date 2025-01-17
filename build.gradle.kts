val ktorVersion = "1.3.2"

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "sbolParserApi.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.apache.commons:commons-text:1.8")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    runtime(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.5")
    implementation(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.5")
    implementation("org.sbolstandard:libSBOLj:2.3.1")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-xml", version = "2.9.4")
    implementation("com.squareup.okhttp3:okhttp:4.8.0")
    compile("org.apache.commons:commons-csv:1.8")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileKotlin {
    }
    compileJava {
        // options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
    compileTestKotlin {
    }

    test {
        useJUnitPlatform()
    }
}
