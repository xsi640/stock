import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("kotlin-kapt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jsoup:jsoup:1.14.1")
    implementation("org.postgresql:postgresql")
    implementation("com.querydsl:querydsl-jpa:5.0.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1")
    implementation("org.seleniumhq.selenium:selenium-java:3.141.59")

    kapt("com.querydsl:querydsl-apt:5.0.0")
}

val bootJar: BootJar by tasks
bootJar.enabled = true