import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.attributes.Category
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.configureEach
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.konan.target.HostManager
import javax.inject.Inject

class PublishingConventionsPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory,
) : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(DokkaPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        val extension = project.extensions.create<PublishingConventionsExtension>(
            "publishing-convention-extension",
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
            val functionalTestRepository = project.layout.buildDirectory.dir(FUNCTIONAL_TEST_REPOSITORY_PATH)
            val generatedPom = project.objects.fileCollection()
            val staging = project.tasks.register<Sync>(STAGE_JVM_RUNTIME_TASK_NAME) {
                description = "Stages the JVM runtime for functional tests"
                from(jvmJar.flatMap { it.archiveFile }) {
                    rename { "$artifactId-$publicationVersion.jar" }
                }
                from(generatedPom) {
                    rename { "$artifactId-$publicationVersion.pom" }
                }
                into(
                    functionalTestRepository.map { repository ->
                        repository.dir(
                            "${publicationGroup.replace('.', '/')}/$artifactId/$publicationVersion",
                        )
                    },
                )
            }

            val generatedPomTaskName = if (HostManager.hostIsSupported) {
                GENERATE_JVM_POM_TASK_NAME
            } else {
                val component = softwareComponentFactory.adhoc(FUNCTIONAL_TEST_JVM_PUBLICATION_NAME)
                fun addVariant(
                    sourceConfigurationName: String,
                    dependencyConfigurationName: String,
                    elementsConfigurationName: String,
                    mavenScope: String,
                ) {
                    val dependencies = project.configurations.dependencyScope(dependencyConfigurationName)
                    project.configurations.named(sourceConfigurationName).configure {
                        allDependencies.configureEach {
                            val publicationDependency = if (this is ProjectDependency) {
                                project.dependencies.create(
                                    "$publicationGroup:$name-jvm:$publicationVersion",
                                )
                            } else {
                                copy()
                            }
                            dependencies.get().dependencies.add(publicationDependency)
                        }
                    }
                    val elements = project.configurations.consumable(elementsConfigurationName) {
                        extendsFrom(dependencies.get())
                        outgoing.artifact(jvmJar)
                    }
                    component.addVariantsFromConfiguration(elements.get()) {
                        mapToMavenScope(mavenScope)
                    }
                }
                addVariant(
                    JVM_API_ELEMENTS_CONFIGURATION,
                    FUNCTIONAL_TEST_JVM_API_DEPENDENCIES_CONFIGURATION,
                    FUNCTIONAL_TEST_JVM_API_ELEMENTS_CONFIGURATION,
                    "compile",
                )
                addVariant(
                    JVM_RUNTIME_ELEMENTS_CONFIGURATION,
                    FUNCTIONAL_TEST_JVM_RUNTIME_DEPENDENCIES_CONFIGURATION,
                    FUNCTIONAL_TEST_JVM_RUNTIME_ELEMENTS_CONFIGURATION,
                    "runtime",
                )
                project.extensions.configure<PublishingExtension> {
                    publications.create<MavenPublication>(FUNCTIONAL_TEST_JVM_PUBLICATION_NAME) {
                        this.artifactId = artifactId
                        from(component)
                    }
                }
                project.tasks.withType<AbstractPublishToMaven>().configureEach {
                    if (publication.name == FUNCTIONAL_TEST_JVM_PUBLICATION_NAME) {
                        enabled = false
                    }
                }
                GENERATE_FUNCTIONAL_TEST_POM_TASK_NAME
            }

            generatedPom.builtBy(generatedPomTaskName)
            project.tasks.withType<GenerateMavenPom>().configureEach {
                if (name == generatedPomTaskName) {
                    generatedPom.from(destination)
                }
            }

            val repositoryDependencies = project.configurations.dependencyScope(
                FUNCTIONAL_TEST_REPOSITORY_DEPENDENCIES_CONFIGURATION_NAME,
            )
            project.configurations.named(JVM_RUNTIME_CLASSPATH_CONFIGURATION).configure {
                allDependencies.withType(ProjectDependency::class.java).configureEach {
                    repositoryDependencies.get().dependencies.add(copy())
                }
            }

            project.configurations.consumable(FUNCTIONAL_TEST_REPOSITORY_ELEMENTS_CONFIGURATION_NAME) {
                extendsFrom(repositoryDependencies.get())
                attributes {
                    attribute(
                        Category.CATEGORY_ATTRIBUTE,
                        project.objects.named(Category::class.java, FUNCTIONAL_TEST_REPOSITORY_CATEGORY),
                    )
                }
                outgoing.artifact(functionalTestRepository) {
                    builtBy(staging)
                }
            }
        }
    }

    private companion object {
        private const val FUNCTIONAL_TEST_REPOSITORY_PATH = "functional-test-repository"
        private const val FUNCTIONAL_TEST_JVM_PUBLICATION_NAME = "functionalTestJvm"
        private const val GENERATE_JVM_POM_TASK_NAME = "generatePomFileForJvmPublication"
        private const val GENERATE_FUNCTIONAL_TEST_POM_TASK_NAME =
            "generatePomFileForFunctionalTestJvmPublication"
        private const val STAGE_JVM_RUNTIME_TASK_NAME = "stageJvmRuntimeForFunctionalTest"
        private const val JVM_API_ELEMENTS_CONFIGURATION = "jvmApiElements"
        private const val JVM_RUNTIME_ELEMENTS_CONFIGURATION = "jvmRuntimeElements"
        private const val FUNCTIONAL_TEST_JVM_API_DEPENDENCIES_CONFIGURATION =
            "functionalTestJvmApiDependencies"
        private const val FUNCTIONAL_TEST_JVM_API_ELEMENTS_CONFIGURATION = "functionalTestJvmApiElements"
        private const val FUNCTIONAL_TEST_JVM_RUNTIME_DEPENDENCIES_CONFIGURATION =
            "functionalTestJvmRuntimeDependencies"
        private const val FUNCTIONAL_TEST_JVM_RUNTIME_ELEMENTS_CONFIGURATION =
            "functionalTestJvmRuntimeElements"
        private const val JVM_RUNTIME_CLASSPATH_CONFIGURATION = "jvmRuntimeClasspath"
        private const val FUNCTIONAL_TEST_REPOSITORY_DEPENDENCIES_CONFIGURATION_NAME =
            "functionalTestRepositoryDependencies"
        private const val FUNCTIONAL_TEST_REPOSITORY_ELEMENTS_CONFIGURATION_NAME =
            "functionalTestRepositoryElements"
        private const val FUNCTIONAL_TEST_REPOSITORY_CATEGORY = "functional-test-repository"
    }
}
