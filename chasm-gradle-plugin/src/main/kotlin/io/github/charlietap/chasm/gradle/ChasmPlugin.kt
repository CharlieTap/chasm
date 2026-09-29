package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.chasm_gradle_plugin.BuildConfig
import org.gradle.api.InvalidUserCodeException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("chasm", ChasmExtension::class.java, project.objects)
        val workerClasspath = createWorkerClasspathConfiguration(project)

        project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
            val multiplatform = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val commonMain = multiplatform.sourceSets.getByName(COMMON_MAIN_SOURCE_SET_NAME)
            val hasWebTarget = project.objects.property(Boolean::class.java).convention(false)
            val unsupportedWasiTargets = project.objects.setProperty(String::class.java).convention(emptySet())
            hasWebTarget.disallowUnsafeRead()
            unsupportedWasiTargets.disallowUnsafeRead()

            multiplatform.targets.configureEach { target ->
                if (target.platformType == KotlinPlatformType.js || target.platformType == KotlinPlatformType.wasm) {
                    hasWebTarget.set(true)
                }
                if (!target.supportsWasiPreview1()) {
                    unsupportedWasiTargets.add(target.name)
                }
            }

            extension.modules.configureEach { module ->
                val config = validatedCodegenConfig(
                    module.codegenConfig,
                    hasWebTarget,
                    unsupportedWasiTargets,
                )
                addRuntime(
                    project = project,
                    config = config,
                    selection = extension.runtimeDependencyConfiguration,
                    apiConfigurationName = commonMain.apiConfigurationName,
                    implementationConfigurationName = commonMain.implementationConfigurationName,
                )
                val task = registerCodegenTask(
                    project = project,
                    module = module,
                    sourceSetName = COMMON_MAIN_SOURCE_SET_NAME,
                    classpath = workerClasspath,
                    config = config,
                )
                commonMain.kotlin.srcDir(task.flatMap(CodegenTask::outputDirectory))
            }
        }

        project.pluginManager.withPlugin(KOTLIN_JVM_PLUGIN_ID) {
            val kotlin = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = kotlin.target.compilations.getByName(MAIN_COMPILATION_NAME)

            extension.modules.configureEach { module ->
                val config = validatedCodegenConfig(module.codegenConfig)
                addRuntime(
                    project = project,
                    config = config,
                    selection = extension.runtimeDependencyConfiguration,
                    apiConfigurationName = API_CONFIGURATION_NAME,
                    implementationConfigurationName = IMPLEMENTATION_CONFIGURATION_NAME,
                    jvmArtifact = true,
                )
                val task = registerCodegenTask(
                    project = project,
                    module = module,
                    sourceSetName = MAIN_COMPILATION_NAME,
                    classpath = workerClasspath,
                    config = config,
                )
                mainCompilation.defaultSourceSet.kotlin.srcDir(task.flatMap(CodegenTask::outputDirectory))
            }
        }

        project.pluginManager.withPlugin(ANDROID_BASE_PLUGIN_ID) {
            extension.modules.configureEach { module ->
                val config = validatedCodegenConfig(module.codegenConfig)
                addRuntime(
                    project = project,
                    config = config,
                    selection = extension.runtimeDependencyConfiguration,
                    apiConfigurationName = API_CONFIGURATION_NAME,
                    implementationConfigurationName = IMPLEMENTATION_CONFIGURATION_NAME,
                )
            }
            configureAndroid(project, extension, workerClasspath) { config ->
                validatedCodegenConfig(config)
            }
        }
    }

    private fun createWorkerClasspathConfiguration(project: Project): Provider<out Configuration> {
        val dependencies = project.configurations.dependencyScope(WORKER_DEPENDENCIES_CONFIGURATION_NAME) { configuration ->
            configuration.description = "Dependencies for the Chasm codegen worker"
        }
        project.dependencies.add(dependencies.name, BuildConfig.CHASM_JVM_DEPENDENCY)
        project.dependencies.add(dependencies.name, BuildConfig.VM_JVM_DEPENDENCY)
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

    private fun addRuntime(
        project: Project,
        config: Provider<CodegenConfig>,
        selection: Provider<RuntimeDependencyConfiguration>,
        apiConfigurationName: String,
        implementationConfigurationName: String,
        jvmArtifact: Boolean = false,
    ) {
        val runtimeNotation = config.map { value ->
            when (value.runtime) {
                CodegenRuntime.PORTABLE_VM -> resolveVMRuntimeNotation(jvmArtifact)
                CodegenRuntime.CHASM -> resolveChasmRuntimeNotation(jvmArtifact)
            }
        }
        project.dependencies.addProvider(
            apiConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.API }.flatMap { runtimeNotation },
        )
        project.dependencies.addProvider(
            implementationConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.IMPLEMENTATION }.flatMap { runtimeNotation },
        )

        val coroutineNotation = config.filter { value ->
            value.runtime == CodegenRuntime.CHASM && value.generateSuspendingFactories
        }
            .map { resolveChasmCoroutinesRuntimeNotation(jvmArtifact) }
        project.dependencies.addProvider(
            apiConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.API }.flatMap { coroutineNotation },
        )
        project.dependencies.addProvider(
            implementationConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.IMPLEMENTATION }.flatMap { coroutineNotation },
        )

        val wasiPreview1Notation = config.filter { value -> value.wasi == WasiLinking.AUTOMATIC }
            .map { resolveWasiPreview1RuntimeNotation() }
        project.dependencies.addProvider(
            apiConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.API }.flatMap { wasiPreview1Notation },
        )
        project.dependencies.addProvider(
            implementationConfigurationName,
            selection.filter { it == RuntimeDependencyConfiguration.IMPLEMENTATION }.flatMap { wasiPreview1Notation },
        )
    }

    private fun validatedCodegenConfig(config: Provider<CodegenConfig>): Provider<CodegenConfig> = config.map { value ->
        validateWasiRuntime(value)
    }

    private fun validatedCodegenConfig(
        config: Provider<CodegenConfig>,
        hasWebTarget: Provider<Boolean>,
        unsupportedWasiTargets: Provider<Set<String>>,
    ): Provider<CodegenConfig> = config.zip(
        hasWebTarget.zip(unsupportedWasiTargets) { webTarget, unsupportedTargets ->
            TargetCompatibility(webTarget, unsupportedTargets)
        },
    ) { configuredValue, compatibility ->
        val value = validateWasiRuntime(configuredValue)
        if (value.wasi == WasiLinking.AUTOMATIC && compatibility.unsupportedWasiTargets.isNotEmpty()) {
            throw InvalidUserCodeException(
                "The current Preview 1 provider for WasiLinking.AUTOMATIC does not support targets: " +
                    compatibility.unsupportedWasiTargets.sorted().joinToString(", ") + ". " +
                    "Use WasiLinking.DISABLED or move the generated module to a supported source set.",
            )
        }
        if (value.runtime == CodegenRuntime.CHASM && compatibility.hasWebTarget) {
            throw InvalidUserCodeException(
                "CodegenRuntime.CHASM only supports Chasm's JVM, Android, and Kotlin/Native targets. " +
                    "Use CodegenRuntime.PORTABLE_VM for modules generated in a source set shared with JS or Wasm JS.",
            )
        }
        value
    }

    private fun validateWasiRuntime(config: CodegenConfig): CodegenConfig {
        if (config.wasi == WasiLinking.AUTOMATIC && config.runtime != CodegenRuntime.CHASM) {
            throw InvalidUserCodeException(
                "WasiLinking.AUTOMATIC requires CodegenRuntime.CHASM because the WASI binding uses " +
                    "Chasm's direct host-function API.",
            )
        }
        return config
    }

    private fun resolveVMRuntimeNotation(jvmArtifact: Boolean = false): String {
        return if (jvmArtifact) BuildConfig.VM_JVM_DEPENDENCY else BuildConfig.VM_DEPENDENCY
    }

    private fun resolveChasmRuntimeNotation(jvmArtifact: Boolean = false): String {
        return if (jvmArtifact) BuildConfig.CHASM_JVM_DEPENDENCY else BuildConfig.CHASM_DEPENDENCY
    }

    private fun resolveChasmCoroutinesRuntimeNotation(jvmArtifact: Boolean = false): String {
        return if (jvmArtifact) {
            BuildConfig.CHASM_COROUTINES_JVM_DEPENDENCY
        } else {
            BuildConfig.CHASM_COROUTINES_DEPENDENCY
        }
    }

    private fun resolveWasiPreview1RuntimeNotation(): String = BuildConfig.WASI_PREVIEW1_DEPENDENCY

    private fun resolveKotlinPoetNotation(): String {
        return BuildConfig.KOTLIN_POET_DEPENDENCY
    }

    private data class TargetCompatibility(
        val hasWebTarget: Boolean,
        val unsupportedWasiTargets: Set<String>,
    )

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

private fun KotlinTarget.supportsWasiPreview1(): Boolean = when (platformType) {
    KotlinPlatformType.jvm,
    KotlinPlatformType.androidJvm,
    KotlinPlatformType.common,
    -> true
    KotlinPlatformType.native -> (this as KotlinNativeTarget).konanTarget in WASI_PREVIEW1_NATIVE_TARGETS
    KotlinPlatformType.js,
    KotlinPlatformType.wasm,
    -> false
}

private val WASI_PREVIEW1_NATIVE_TARGETS = setOf(
    KonanTarget.IOS_ARM64,
    KonanTarget.IOS_SIMULATOR_ARM64,
    KonanTarget.LINUX_ARM64,
    KonanTarget.LINUX_X64,
    KonanTarget.MACOS_ARM64,
)
