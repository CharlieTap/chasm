package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.chasm_gradle_plugin.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create("chasm", ChasmExtension::class.java, project.objects)

        val workerClasspath = createWorkerClasspathConfiguration(project)

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val mpp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            project.afterEvaluate {
                extension.modules.configureEach { module ->
                    val commonMainSourceSet = mpp.sourceSets.getByName("commonMain")
                    addVMRuntimeForKmp(project, extension.runtimeDependencyConfiguration.get(), commonMainSourceSet)

                    val task = registerCodegenTask(
                        project,
                        module,
                        "commonMain",
                        workerClasspath,
                    )
                    commonMainSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })
                }
            }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val jvmExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = jvmExtension.target.compilations.getByName(MAIN_COMPILATION_NAME)

            addVMRuntimeForJvmOrAndroid(project, extension.runtimeDependencyConfiguration.get())

            project.afterEvaluate {
                extension.modules.configureEach { module ->
                    val task = registerCodegenTask(
                        project,
                        module,
                        MAIN_COMPILATION_NAME,
                        workerClasspath,
                    )
                    mainCompilation.defaultSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })
                }
            }
        }

        project.plugins.withId("com.android.base") {
            val agpVersion = AgpVersion.detect()
                ?: throw GradleException("Chasm Gradle plugin requires Android Gradle Plugin on the classpath.")
            val configurer = loadAndroidConfigurer(agpVersion)

            addVMRuntimeForJvmOrAndroid(project, extension.runtimeDependencyConfiguration.get())

            val androidComponents = project.extensions.getByName("androidComponents")
            configurer.configure(
                androidComponents = androidComponents,
                context = AndroidConfigContext(
                    project = project,
                    extension = extension,
                    workerClasspath = workerClasspath,
                ),
            )
        }
    }

    private fun createWorkerClasspathConfiguration(project: Project): Configuration {
        val dependencies = project.configurations.dependencyScope(WORKER_DEPENDENCIES_CONFIGURATION_NAME) { configuration ->
            configuration.description = "Dependencies for the chasm codegen worker"
        }
        project.dependencies.add(dependencies.name, resolveChasmRuntimeNotation())
        project.dependencies.add(dependencies.name, resolveVMRuntimeNotation())

        return project.configurations.resolvable(WORKER_CLASSPATH_CONFIGURATION_NAME) { configuration ->
            configuration.description = "Classpath for the chasm codegen worker"
            configuration.extendsFrom(dependencies.get())

            configuration.attributes { attributes ->
                attributes.attribute(
                    Usage.USAGE_ATTRIBUTE,
                    project.objects.named(Usage::class.java, Usage.JAVA_RUNTIME),
                )
                attributes.attribute(
                    Category.CATEGORY_ATTRIBUTE,
                    project.objects.named(Category::class.java, Category.LIBRARY),
                )
            }
        }.get()
    }

    private fun loadAndroidConfigurer(agpVersion: AgpVersion): AndroidConfigurer {
        if (agpVersion.major < 8) {
            throw GradleException("Chasm Gradle plugin requires AGP 8.x or newer. Found $agpVersion.")
        }

        val implementationClass = when (agpVersion.major) {
            8 -> "io.github.charlietap.chasm.gradle.agp.Agp8AndroidConfigurer"
            9 -> "io.github.charlietap.chasm.gradle.agp.Agp9AndroidConfigurer"
            else -> null
        } ?: throw GradleException("Chasm Gradle plugin does not support AGP $agpVersion.")

        return runCatching {
            val implClass = Class.forName(implementationClass, true, javaClass.classLoader)
            implClass.getDeclaredConstructor().newInstance() as AndroidConfigurer
        }.getOrElse { error ->
            throw GradleException(
                "Failed to load Android integration for AGP $agpVersion. " +
                    "Ensure the chasm Gradle plugin artifacts are on the classpath.",
                error,
            )
        }
    }

    private fun addVMRuntimeForKmp(
        project: Project,
        configuration: RuntimeDependencyConfiguration,
        commonMain: KotlinSourceSet,
    ) {
        val notation = resolveVMRuntimeNotation()
        val configurationName = when (configuration) {
            RuntimeDependencyConfiguration.API -> commonMain.apiConfigurationName
            RuntimeDependencyConfiguration.IMPLEMENTATION -> commonMain.implementationConfigurationName
        }
        val exists = project.configurations.getByName(configurationName).dependencies.any {
            it.group == RUNTIME_GROUP && it.name == RUNTIME_ARTIFACT
        }
        if (!exists) {
            project.dependencies.add(configurationName, notation)
        }
    }

    private fun addVMRuntimeForJvmOrAndroid(
        project: Project,
        configuration: RuntimeDependencyConfiguration,
    ) {
        val configurationName = configuration.name.lowercase()
        val notation = resolveVMRuntimeNotation(RUNTIME_JVM_ARTIFACT_SUFFIX)
        if (!runtimeDependencyExists(project, configurationName)) {
            project.dependencies.add(configurationName, notation)
        }
    }

    private fun resolveVMRuntimeNotation(
        suffix: String? = null,
    ): Any {
        val group = RUNTIME_GROUP
        val artifact = suffix?.let {
            "$RUNTIME_ARTIFACT-$suffix"
        } ?: RUNTIME_ARTIFACT
        val version = BuildConfig.RUNTIME_VERSION
        return "$group:$artifact:$version"
    }

    private fun resolveChasmRuntimeNotation(): Any {
        val group = RUNTIME_GROUP
        val artifact = CHASM_ARTIFACT
        val version = BuildConfig.RUNTIME_VERSION
        return "$group:$artifact:$version"
    }

    private fun runtimeDependencyExists(
        project: Project,
        configurationName: String,
    ): Boolean {
        val dependencies = project.configurations.findByName(configurationName)?.allDependencies.orEmpty()
        return dependencies.any { dep ->
            dep.group == RUNTIME_GROUP && (dep.name == RUNTIME_ARTIFACT || dep.name == "$RUNTIME_ARTIFACT-$RUNTIME_JVM_ARTIFACT_SUFFIX")
        }
    }

    private companion object {
        private const val RUNTIME_GROUP = "io.github.charlietap.chasm"
        private const val RUNTIME_ARTIFACT = "vm"
        private const val RUNTIME_JVM_ARTIFACT_SUFFIX = "jvm"
        private const val CHASM_ARTIFACT = "chasm"
        private const val WORKER_DEPENDENCIES_CONFIGURATION_NAME = "chasmCodegenWorkerDependencies"
        private const val WORKER_CLASSPATH_CONFIGURATION_NAME = "chasmCodegenWorkerClasspath"
    }
}
