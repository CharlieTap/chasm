package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.chasm_gradle_plugin.BuildConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("chasm", ChasmExtension::class.java, project.objects)
        val workerClasspath = createWorkerClasspathConfiguration(project)

        project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            val multiplatform = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val commonMain = multiplatform.sourceSets.getByName(COMMON_MAIN_SOURCE_SET_NAME)

            addVMRuntime(
                project = project,
                selection = extension.runtimeDependencyConfiguration,
                apiConfigurationName = commonMain.apiConfigurationName,
                implementationConfigurationName = commonMain.implementationConfigurationName,
            )

            extension.modules.configureEach { module ->
                val task = registerCodegenTask(
                    project,
                    module,
                    COMMON_MAIN_SOURCE_SET_NAME,
                    workerClasspath,
                )
                commonMain.kotlin.srcDir(task.flatMap(CodegenTask::outputDirectory))
            }
        }

        project.pluginManager.withPlugin(KOTLIN_JVM_PLUGIN_ID) {
            val kotlin = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = kotlin.target.compilations.getByName(MAIN_COMPILATION_NAME)

            addVMRuntime(
                project = project,
                selection = extension.runtimeDependencyConfiguration,
                apiConfigurationName = API_CONFIGURATION_NAME,
                implementationConfigurationName = IMPLEMENTATION_CONFIGURATION_NAME,
                jvmArtifact = true,
            )

            extension.modules.configureEach { module ->
                val task = registerCodegenTask(
                    project,
                    module,
                    MAIN_COMPILATION_NAME,
                    workerClasspath,
                )
                mainCompilation.defaultSourceSet.kotlin.srcDir(task.flatMap(CodegenTask::outputDirectory))
            }
        }

        project.pluginManager.withPlugin(ANDROID_BASE_PLUGIN_ID) {
            addVMRuntime(
                project = project,
                selection = extension.runtimeDependencyConfiguration,
                apiConfigurationName = API_CONFIGURATION_NAME,
                implementationConfigurationName = IMPLEMENTATION_CONFIGURATION_NAME,
            )
            configureAndroid(project, extension, workerClasspath)
        }
    }

    private fun createWorkerClasspathConfiguration(project: Project): Provider<out Configuration> {
        val dependencies = project.configurations.dependencyScope(WORKER_DEPENDENCIES_CONFIGURATION_NAME) { configuration ->
            configuration.description = "Dependencies for the Chasm codegen worker"
        }
        project.dependencies.add(dependencies.name, resolveChasmRuntimeNotation())
        project.dependencies.add(dependencies.name, resolveVMRuntimeNotation(jvmArtifact = true))
        project.dependencies.add(dependencies.name, resolveKotlinPoetNotation())

        return project.configurations.resolvable(WORKER_CLASSPATH_CONFIGURATION_NAME) { configuration ->
            configuration.description = "Classpath for the Chasm codegen worker"
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
        }
    }

    private fun addVMRuntime(
        project: Project,
        selection: Provider<RuntimeDependencyConfiguration>,
        apiConfigurationName: String,
        implementationConfigurationName: String,
        jvmArtifact: Boolean = false,
    ) {
        val notation = resolveVMRuntimeNotation(jvmArtifact)
        project.dependencies.addProvider(
            apiConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.API }.map { notation },
        )
        project.dependencies.addProvider(
            implementationConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.IMPLEMENTATION }.map { notation },
        )
    }

    private fun resolveVMRuntimeNotation(jvmArtifact: Boolean = false): String {
        return if (jvmArtifact) BuildConfig.VM_JVM_DEPENDENCY else BuildConfig.VM_DEPENDENCY
    }

    private fun resolveChasmRuntimeNotation(): String {
        return BuildConfig.CHASM_JVM_DEPENDENCY
    }

    private fun resolveKotlinPoetNotation(): String {
        return BuildConfig.KOTLIN_POET_DEPENDENCY
    }

    private companion object {
        private const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
        private const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
        private const val ANDROID_BASE_PLUGIN_ID = "com.android.base"
        private const val COMMON_MAIN_SOURCE_SET_NAME = "commonMain"
        private const val API_CONFIGURATION_NAME = "api"
        private const val IMPLEMENTATION_CONFIGURATION_NAME = "implementation"
        private const val WORKER_DEPENDENCIES_CONFIGURATION_NAME = "chasmCodegenWorkerDependencies"
        private const val WORKER_CLASSPATH_CONFIGURATION_NAME = "chasmCodegenWorkerClasspath"
    }
}
