import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.4.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
    kotlin("plugin.jpa") version "1.5.0"
    kotlin("kapt") version "1.5.0" apply false
}

allprojects {

    apply {
        plugin("idea")
        plugin("java")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
        plugin("kotlin-allopen")
        plugin("kotlin-noarg")
    }

    group = "com.github.xsi640"
    version = "1.0.0"
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8


    val vers = mapOf(
        "commons_io" to "2.8.0",
        "commons_codec" to "1.15",
        "commons_lang" to "3.12.0",
        "jackson" to "2.11.3"
    )

    rootProject.extra.set("vers", vers)
    rootProject.extra.set("docker_registry", "192.168.1.254:5000")
    rootProject.extra.set("docker_host", "192.168.1.254:2376")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
        implementation("commons-io:commons-io:${vers["commons_io"]}")
        implementation("commons-codec:commons-codec:${vers["commons_codec"]}")
        implementation("org.apache.commons:commons-lang3:${vers["commons_lang"]}")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("org.jetbrains.kotlin:kotlin-allopen")
        implementation("org.jetbrains.kotlin:kotlin-noarg")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("junit:junit")

    }

    val user = System.getProperty("repoUser")
    val pwd = System.getProperty("repoPassword")

    repositories {
        mavenLocal()
        maven {
            credentials {
                username = user
                password = pwd
                isAllowInsecureProtocol = true
            }
            url = uri("http://192.168.1.254:8081/repository/maven-group/")
//            url = uri("http://172.16.11.231:8081/nexus/repository/maven2-group/")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    val jar: Jar by tasks
    val bootJar: BootJar by tasks
    jar.enabled = true
    bootJar.enabled = false
}

apply(from = rootProject.file("buildscript/root.gradle.kts"))