import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaPlugin

class PublishingConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(DokkaPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        val extension = project.extensions.create<PublishingConventionsExtension>(
            "publishing-convention-extension",
        )
        extension.functionalTestRepository.convention(
            project.layout.settingsDirectory.dir(FUNCTIONAL_TEST_REPOSITORY_PATH),
        )

        project.group = "io.github.charlietap.chasm"
        if (project.version == DEFAULT_VERSION) {
            project.version = project.extensions
                .getByType<VersionCatalogsExtension>()
                .find("libs")
                .get()
                .findVersion("version-name")
                .get()
                .requiredVersion
        }

        project.configure<MavenPublishBaseExtension> {

            publishToMavenCentral()

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

        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val publicationGroup = project.group.toString()
            val publicationVersion = project.version.toString()

            val artifactId = "${project.name}-jvm"
            val jvmJar = project.tasks.named<Jar>("jvmJar")
            val generatedPom = project.objects.fileCollection().builtBy(GENERATE_JVM_POM_TASK_NAME)
            val staging = project.tasks.register<Sync>(PUBLISH_JVM_RUNTIME_TASK_NAME) {
                description = "Stages the JVM runtime for functional tests"
                from(jvmJar.flatMap { it.archiveFile }) {
                    rename { "$artifactId-$publicationVersion.jar" }
                }
                from(generatedPom) {
                    rename { "$artifactId-$publicationVersion.pom" }
                }
                into(
                    extension.functionalTestRepository.dir(
                        "${publicationGroup.replace('.', '/')}/$artifactId/$publicationVersion",
                    ),
                )
            }

            project.tasks.withType<GenerateMavenPom>().configureEach {
                if (name == GENERATE_JVM_POM_TASK_NAME) {
                    generatedPom.from(destination)
                }
            }

            val dependencyPublications = project.configurations
                .named(JVM_RUNTIME_CLASSPATH_CONFIGURATION)
                .map { configuration ->
                    configuration.allDependencies
                        .withType(ProjectDependency::class.java)
                        .map { dependency ->
                            "${dependency.path}:$PUBLISH_JVM_RUNTIME_TASK_NAME"
                        }
                }

            staging.configure {
                dependsOn(dependencyPublications)
            }
        }
    }

    private companion object {
        private const val FUNCTIONAL_TEST_REPOSITORY_PATH = "build/functional-test-repository"
        private const val GENERATE_JVM_POM_TASK_NAME = "generatePomFileForJvmPublication"
        private const val PUBLISH_JVM_RUNTIME_TASK_NAME = "publishJvmRuntimeToFunctionalTestRepository"
        private const val JVM_RUNTIME_CLASSPATH_CONFIGURATION = "jvmRuntimeClasspath"
    }
}
