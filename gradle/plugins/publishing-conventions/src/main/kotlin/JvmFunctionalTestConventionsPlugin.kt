import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.configureEach
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.HostManager
import javax.inject.Inject

class JvmFunctionalTestConventionsPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory,
) : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            configureJvmProjectRepository(project)
        }
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            val kotlin = project.extensions.getByType<KotlinMultiplatformExtension>()
            kotlin.targets
                .withType<KotlinJvmTarget>()
                .matching { target -> target.name == JVM_TARGET_NAME }
                .configureEach {
                    configureFunctionalTestRepository(project, kotlin, this)
                }
        }
    }

    private fun configureFunctionalTestRepository(
        project: Project,
        kotlin: KotlinMultiplatformExtension,
        target: KotlinJvmTarget,
    ) {
        val artifactId = "${project.name}-jvm"
        val jvmJar = project.tasks.named<Jar>(target.artifactsTaskName)
        val publishesTargets = HostManager.hostIsSupported || kotlin.targets.withType<KotlinNativeTarget>().isEmpty()
        val pomPublicationName = if (publishesTargets) {
            target.name
        } else {
            configureFunctionalTestPublication(project, target, jvmJar, artifactId)
            FUNCTIONAL_TEST_JVM_PUBLICATION_NAME
        }

        val staging = registerStaging(
            project = project,
            jar = jvmJar,
            pom = publicationPom(project, pomPublicationName),
            artifactId = project.provider { artifactId },
        )
        val mainCompilation = target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)
        configureRepositoryElements(
            project = project,
            runtimeClasspathConfigurationName = mainCompilation.runtimeDependencyConfigurationName,
            staging = staging,
        )
    }

    private fun configureFunctionalTestPublication(
        project: Project,
        target: KotlinJvmTarget,
        jvmJar: TaskProvider<Jar>,
        artifactId: String,
    ) {
        val publicationGroup = project.group.toString()
        val publicationVersion = project.version.toString()
        val component = softwareComponentFactory.adhoc(FUNCTIONAL_TEST_JVM_PUBLICATION_NAME)
        fun addVariant(
            sourceConfigurationName: String,
            variantName: String,
            usage: String,
            mavenScope: String,
        ) {
            val dependencies = project.configurations.dependencyScope("${variantName}Dependencies")
            project.configurations.named(sourceConfigurationName).configure {
                allDependencies.configureEach {
                    val publicationDependency = if (this is ProjectDependency) {
                        project.dependencies.create("$publicationGroup:$name-jvm:$publicationVersion")
                    } else {
                        copy()
                    }
                    dependencies.get().dependencies.add(publicationDependency)
                }
            }
            val elements = project.configurations.consumable("${variantName}Elements") {
                extendsFrom(dependencies.get())
                attributes {
                    attribute(
                        Category.CATEGORY_ATTRIBUTE,
                        project.objects.named(Category::class.java, FUNCTIONAL_TEST_POM_CATEGORY),
                    )
                    attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, usage))
                }
                outgoing.capability("$publicationGroup:${project.name}-functional-test-pom:$publicationVersion")
                outgoing.artifact(jvmJar)
            }
            component.addVariantsFromConfiguration(elements) {
                mapToMavenScope(mavenScope)
            }
        }
        addVariant(target.apiElementsConfigurationName, "functionalTestJvmApi", Usage.JAVA_API, "compile")
        addVariant(target.runtimeElementsConfigurationName, "functionalTestJvmRuntime", Usage.JAVA_RUNTIME, "runtime")

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
    }

    private fun configureJvmProjectRepository(project: Project) {
        val staging = registerStaging(
            project = project,
            jar = project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME),
            pom = publicationPom(project, MAVEN_PUBLICATION_NAME),
            artifactId = publishedArtifactId(project, MAVEN_PUBLICATION_NAME, project.name),
        )
        configureRepositoryElements(
            project = project,
            runtimeClasspathConfigurationName = JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME,
            staging = staging,
        )
    }

    private fun registerStaging(
        project: Project,
        jar: TaskProvider<Jar>,
        pom: FileCollection,
        artifactId: Provider<String>,
    ): TaskProvider<Sync> {
        val publicationGroup = project.group.toString()
        val publicationVersion = project.version.toString()
        return project.tasks.register<Sync>(STAGE_JVM_RUNTIME_TASK_NAME) {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Stages the JVM runtime for functional tests"
            from(jar.flatMap { it.archiveFile }) {
                rename { "${artifactId.get()}-$publicationVersion.jar" }
            }
            from(pom) {
                rename { "${artifactId.get()}-$publicationVersion.pom" }
            }
            into(
                project.layout.buildDirectory.dir(FUNCTIONAL_TEST_REPOSITORY_PATH).zip(artifactId) { repository, id ->
                    repository.dir("${publicationGroup.replace('.', '/')}/$id/$publicationVersion")
                },
            )
        }
    }

    private fun configureRepositoryElements(
        project: Project,
        runtimeClasspathConfigurationName: String,
        staging: TaskProvider<Sync>,
    ) {
        val repositoryDependencies = project.configurations.dependencyScope(
            FUNCTIONAL_TEST_REPOSITORY_DEPENDENCIES_CONFIGURATION_NAME,
        )
        project.configurations.named(runtimeClasspathConfigurationName).configure {
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
            outgoing.artifact(project.layout.buildDirectory.dir(FUNCTIONAL_TEST_REPOSITORY_PATH)) {
                builtBy(staging)
            }
        }
    }

    private fun publicationPom(
        project: Project,
        publicationName: String,
    ): FileCollection = project.files(
        project.tasks.withType<GenerateMavenPom>().matching { task ->
            val publication = project.extensions.getByType<PublishingExtension>().publications.findByName(publicationName)
            publication is MavenPublication && task.pom === publication.pom
        },
    )

    private fun publishedArtifactId(
        project: Project,
        publicationName: String,
        default: String,
    ): Provider<String> = project.provider {
        val publications = project.extensions.findByType<PublishingExtension>()?.publications
        (publications?.findByName(publicationName) as? MavenPublication)?.artifactId ?: default
    }

    private companion object {
        private const val MAVEN_PUBLICATION_NAME = "maven"
        private const val JVM_TARGET_NAME = "jvm"
        private const val FUNCTIONAL_TEST_REPOSITORY_PATH = "functional-test-repository"
        private const val FUNCTIONAL_TEST_JVM_PUBLICATION_NAME = "functionalTestJvm"
        private const val FUNCTIONAL_TEST_POM_CATEGORY = "functional-test-pom"
        private const val STAGE_JVM_RUNTIME_TASK_NAME = "stageJvmRuntimeForFunctionalTest"
        private const val FUNCTIONAL_TEST_REPOSITORY_DEPENDENCIES_CONFIGURATION_NAME =
            "functionalTestRepositoryDependencies"
        private const val FUNCTIONAL_TEST_REPOSITORY_ELEMENTS_CONFIGURATION_NAME =
            "functionalTestRepositoryElements"
        private const val FUNCTIONAL_TEST_REPOSITORY_CATEGORY = "functional-test-repository"
    }
}
