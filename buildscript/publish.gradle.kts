import org.gradle.util.VersionNumber
import org.gradle.api.tasks.bundling.Jar

val user = System.getProperty("repoUser")
val pwd = System.getProperty("repoPassword")

apply<MavenPublishPlugin>()

if (user != null && pwd != null) {
    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
    }
    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                create<MavenPublication>(project.name) {
                    from(components["java"])
                    artifact(sourcesJar.get())
                    pom {
                        name.set(project.name)
                        packaging = "jar"
                    }
                }
            }
            repositories {
                maven {
                    credentials {
                        username = user
                        password = pwd
                        isAllowInsecureProtocol = true
                    }
                    url = uri(
                        if (VersionNumber.parse(project.version.toString()).qualifier == "SNAPSHOT") {
                            "http://192.168.1.254:8081/nexus/repository/maven-snapshot/"
                        } else {
                            "http://192.168.1.254:8081/nexus/repository/maven-release/"
                        }
                    )
                }
            }
        }
    }

    task("showPublishingJar") {
        doFirst {
            println("Publish jar ${project.group}:${project.name}:${project.version}")
        }
    }
    tasks.getByName("publish").dependsOn("showPublishingJar")
}