
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.create
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

class PublishingConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.pluginManager.apply(DokkaPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        val extension = project.extensions.create<PublishingConventionsExtension>("publishing-convention-extension")

        project.group = "io.github.charlietap.chasm"
        if(project.version == DEFAULT_VERSION) {
            project.version = project.extensions.getByType(VersionCatalogsExtension::class.java).find("libs").get().findVersion("version-name").get().requiredVersion
        }

        project.tasks.withType<DokkaTask>().configureEach {
            notCompatibleWithConfigurationCache("https://github.com/Kotlin/dokka/issues/2231")
        }

        project.configure<MavenPublishBaseExtension> {

            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

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
}
