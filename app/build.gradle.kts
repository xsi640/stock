import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("kotlin-kapt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jsoup:jsoup:1.14.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")

    api("com.querydsl:querydsl-jpa:4.4.0")
    kapt("com.querydsl:querydsl-apt:4.4.0:jpa")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
}

val bootJar: BootJar by tasks
bootJar.enabled = true