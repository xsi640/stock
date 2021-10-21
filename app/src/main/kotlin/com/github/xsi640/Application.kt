package com.github.xsi640

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplicationBuilder(Application::class.java)
        .web(WebApplicationType.NONE)
        .run(*args)
}