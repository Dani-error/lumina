import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.lang.Integer.parseInt

plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("com.vanniktech.maven.publish") version "0.33.0"
}

defaultTasks("clean", "build")


allprojects {
    group = project.property("GROUP")!!.toString()
    version = project.property("VERSION_NAME")!!.toString()


    repositories {
        mavenCentral()
    }
}

val javaVersion = project.property("JAVA_VERSION")!!.toString()


subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    if (project.name != "platform") {
        apply(plugin = "com.vanniktech.maven.publish")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
        }
    }

    tasks.withType<JavaCompile> {
        options.release.set(parseInt(javaVersion))
    }

    tasks.withType<Jar> {
        from(rootProject.file("LICENSE"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    if (project.name != "platform") {
        extensions.configure<MavenPublishBaseExtension> {
            publishToMavenCentral()
            signAllPublications()

            publishing {
                repositories {
                    maven {
                        name = "githubPackages"
                        url = uri("https://maven.pkg.github.com/Dani-error/lumina")
                        credentials(PasswordCredentials::class)
                    }
                }
            }

            pom {
                name = "Lumina"
                description = project.property("DESCRIPTION")!!.toString()
                inceptionYear = project.property("INCEPTION_YEAR")!!.toString()
                url = project.property("PROJECT_URL")!!.toString()
                licenses {
                    license {
                        name = project.property("LICENSE_NAME")!!.toString()
                        url = project.property("LICENSE_URL")!!.toString()
                    }
                }
                ciManagement {
                    system.set(project.property("CI_SYSTEM")!!.toString())
                    url.set(project.property("CI_URL")!!.toString())
                }
                developers {
                    developer {
                        id = project.property("DEVELOPER_ID")!!.toString()
                        name = project.property("DEVELOPER_NAME")!!.toString()
                        url = project.property("DEVELOPER_URL")!!.toString()
                    }
                }
                scm {
                    val repoUrl = project.property("PROJECT_URL")!!.toString()
                    url = repoUrl
                    connection = "scm:git:git://github.com/Dani-error/lumina.git"
                    developerConnection = "scm:git:ssh://git@github.com/Dani-error/lumina.git"
                }
            }
        }
    }

}
