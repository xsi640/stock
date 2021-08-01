val dockerRegistry = rootProject.extra.get("docker_registry") as String
val dockerHost = rootProject.extra.get("docker_host") as String

task("docker") {
    doLast {
        println("cp app file.")
        val app = File(rootProject.buildDir, "package/app")
        delete(app)
        copy {
            val appName = "${rootProject.name}-app-${rootProject.version}.jar"
            from(rootProject.file("app/build/libs/${appName}"))
            into(app)
            rename(appName, "app.jar")
        }

        println("cp Dockerfile")
        copy {
            from(rootProject.file("buildscript/app/Dockerfile"))
            into(app)
        }
        println("cp launch.sh")
        copy {
            from(rootProject.file("buildscript/app/launch.sh"))
            into(app)
        }

        val imageName = "${dockerRegistry}/${rootProject.name}-app:${rootProject.version}"
        println("build app docker image: $imageName")
        println("docker rmi -f $imageName")
        exec {
            commandLine("docker", "rmi", "-f", imageName)
        }
        println("docker build -t $imageName $app")
        exec {
            commandLine("docker", "build", "-t", imageName, app)
        }
        println("docker push $imageName")
        exec {
            commandLine("docker", "push", imageName)
        }

        println("generate docker run script.")
        println("docker host: $dockerHost")
        println("docker image: $dockerRegistry/${rootProject.name}-app:${rootProject.version}");
        println("app_name: ${rootProject.name}-app")

        val binding = mapOf(
            "docker_image" to "$dockerRegistry/${rootProject.name}-app:${rootProject.version}",
            "docker_host" to dockerHost,
            "app_name" to "${rootProject.name}-app"
        )
        buildTemplateFile(
            rootProject.file("buildscript/app/run.sh"),
            File(rootProject.buildDir, "package/app/run.sh"),
            binding
        )

        exec {
            commandLine(File(rootProject.buildDir, "package/app/run.sh"))
        }
    }
}

fun buildTemplateFile(templateFile: File, toFile: File, map: Map<String, String>) {
    val engine = groovy.text.SimpleTemplateEngine()
    val template = engine.createTemplate(templateFile)
    val writable = template.make(map)
    val fw = java.io.FileWriter(toFile)
    writable.writeTo(fw)
    fw.flush()
    fw.close()
}