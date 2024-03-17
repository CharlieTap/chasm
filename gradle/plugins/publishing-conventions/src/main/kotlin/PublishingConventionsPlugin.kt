
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

class PublishingConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.pluginManager.apply(DokkaPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        project.pluginManager.apply(SigningPlugin::class.java)

        val extension = project.extensions.create<PublishingConventionsExtension>("publishing-convention-extension")

        project.group = "io.github.charlietap.chasm"
        project.version = project.extensions.getByType(VersionCatalogsExtension::class.java).find("libs").get().findVersion("version-name").get().requiredVersion

        val dokkaHtml by project.tasks.getting(DokkaTask::class)
        val javadocTask by project.tasks.registering(Jar::class) {
            dependsOn(dokkaHtml)
            archiveClassifier.set("javadoc")
            from(dokkaHtml.outputDirectory)
        }

        project.tasks.withType<AbstractPublishToMaven>().configureEach {
            val signingTasks = project.tasks.withType<Sign>()
            mustRunAfter(signingTasks)
        }

        project.tasks.withType<DokkaTask>().configureEach {
            notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/2231")
        }

        project.afterEvaluate {

            project.extensions.configure(PublishingExtension::class.java) {
                configurePublishing(project, javadocTask, extension)
            }

            project.extensions.configure(SigningExtension::class.java) {
                configureSigning(project, project.extensions.getByType<PublishingExtension>().publications)
            }
        }
    }

    private fun PublishingExtension.configurePublishing(project: Project, javadocJar: TaskProvider<Jar>, extension: PublishingConventionsExtension) {

        val manualFileRepo = project.uri("file://${project.rootProject.layout.buildDirectory.get()}/manual")

        repositories {
            maven {
                name = "manual"
                url = manualFileRepo
            }
        }

        publications.withType<MavenPublication>().configureEach {

            artifact(javadocJar)

            pom {
                name.set(extension.name)
                description.set(extension.description)
                url.set("https://github.com/CharlieTap/chasm")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("CharlieTap")
                        name.set("Charlie Tapping")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/CharlieTap/chasm.git")
                    developerConnection.set("scm:git:ssh://github.com/CharlieTap/chasm.git")
                    url.set("https://github.com/CharlieTap/chasm")
                }
            }
        }
    }

    private fun SigningExtension.configureSigning(project: Project, publications: PublicationContainer) {
        val signingKey: String? by project
        val signingPassword: String? by project

        if(signingKey != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }

        sign(publications)
    }
}
