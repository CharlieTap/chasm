package io.github.charlietap.chasm.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.github.charlietap.chasm.chasm_gradle_plugin.BuildConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import java.io.File
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    @Suppress("DEPRECATION")
    override fun apply(project: Project) {

        val extension = project.extensions.create<ChasmExtension>("chasm")

        val workerClasspath = createWorkerClasspathConfiguration(project)

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val mpp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            project.afterEvaluate {
                extension.modules.configureEach {
                    when (extension.mode.get()) {
                        Mode.CONSUMER -> {
                            val commonMainSourceSet = mpp.sourceSets.getByName("commonMain")
                            addVMRuntimeForKmp(project, extension.runtimeDependencyConfiguration.get(), commonMainSourceSet)

                            val task = registerCodegenTask(project, this, "commonMain", workerClasspath)
                            commonMainSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })
                        }
                        Mode.PRODUCER -> {
                            project.logger.warn(
                                "Producer mode is deprecated and will be removed in a future release. " +
                                    "For a more robust solution, see https://github.com/CharlieTap/glueball",
                            )
                            // For producers, we can't add the common target as it lacks support for wasm
                            // instead we configure every target other than wasm individually
                            mpp.targets.configureEach {
                                addVMRuntimeToKmpTarget(extension.runtimeDependencyConfiguration.get())
                            }

                            val wasmTargets = mpp.targets.filter { target ->
                                target.name.startsWith("wasmJs") || target.name.startsWith("wasmWasi")
                            }
                            val nonWasmSourceSets = mpp.sourceSets.filter {
                                it.name.lowercase().contains("main") &&
                                    !it.name.lowercase().contains("common") &&
                                    !it.name.lowercase().contains("wasm")
                            }

                            if (wasmTargets.isEmpty()) {
                                project.logger.warn("Producer mode requires at least one WASM target (wasmJs or wasmWasi)")
                                return@configureEach
                            }

                            project.tasks.withType(KotlinJsCompile::class.java).configureEach {
                                compilerOptions.freeCompilerArgs.add(
                                    "-Xwasm-use-new-exception-proposal",
                                )
                            }

                            wasmTargets.forEach { target ->
                                val executable = (target as KotlinJsIrTarget)
                                val mjsFile = executable.binaries.first().mainFile.get().asFile
                                val binary = File(mjsFile.parentFile, mjsFile.nameWithoutExtension + ".wasm")

                                val task = registerCodegenTask(project, this, target.name, workerClasspath, binary)
                                task.dependsOn(executable.binaries.first().linkTask)

                                nonWasmSourceSets.forEach { nonWasmSourceSet ->
                                    nonWasmSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })

                                    val targetName = nonWasmSourceSet.name.removeSuffix("Main")
                                    val taskName = "${targetName}ProcessResources"
                                    project.tasks.named(taskName, Copy::class.java).configure {
                                        dependsOn(executable.binaries.first().linkTask)
                                        inputs.file(binary)
                                        from({ binary }) {
                                            rename { _ -> "producer.wasm" }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val jvmExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = jvmExtension.target.compilations.getByName(MAIN_COMPILATION_NAME)

            addVMRuntimeForJvmOrAndroid(project, extension.runtimeDependencyConfiguration.get())

            extension.modules.configureEach {
                if (extension.mode.get() == Mode.PRODUCER) {
                    project.logger.error("Producer mode is only supported for Kotlin Multiplatform projects with WASM targets")
                    return@configureEach
                }

                val task = registerCodegenTask(project, this, MAIN_COMPILATION_NAME, workerClasspath)
                mainCompilation.defaultSourceSet {
                    kotlin.srcDir(task.flatMap { it.outputDirectory })
                }
            }
        }

        project.plugins.withId("com.android.base") {
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

            addVMRuntimeForJvmOrAndroid(project, extension.runtimeDependencyConfiguration.get())

            androidComponents.onVariants { variant: Variant ->
                extension.modules.configureEach {
                    if (extension.mode.get() == Mode.PRODUCER) {
                        project.logger.error("Producer mode is only supported for Kotlin Multiplatform projects with WASM targets")
                        return@configureEach
                    }

                    val task = registerCodegenTask(project, this, variant.name, workerClasspath)
                    variant.sources.java?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
                    variant.sources.kotlin?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
                }
            }
        }

        project.gradle.projectsEvaluated {
            if (System.getProperty("idea.sync.active")?.toBoolean() == true) {
                val tasksToTrigger = project.tasks.withType(CodegenTask::class.java).map { it.name }
                val sp = project.gradle.startParameter
                sp.setTaskNames(sp.taskNames + tasksToTrigger)
            }
        }
    }

    private fun createWorkerClasspathConfiguration(project: Project): Configuration {
        return project.configurations.create(WORKER_CLASSPATH_CONFIGURATION_NAME) {
            isCanBeConsumed = false
            isCanBeResolved = true
            isTransitive = true
            description = "Classpath for the chasm codegen worker"

            attributes {
                attribute(
                    Usage.USAGE_ATTRIBUTE,
                    project.objects.named(Usage::class.java, Usage.JAVA_RUNTIME),
                )
                attribute(
                    Category.CATEGORY_ATTRIBUTE,
                    project.objects.named(Category::class.java, Category.LIBRARY),
                )
            }
        }.also { config ->
            val chasmDep = resolveChasmRuntimeNotation()
            val vmDep = resolveVMRuntimeNotation()
            project.dependencies.add(config.name, chasmDep)
            project.dependencies.add(config.name, vmDep)
        }
    }

    private fun registerCodegenTask(
        project: Project,
        module: WasmModule,
        sourceSetName: String,
        classpath: Configuration,
        moduleBinary: File? = null,
    ): TaskProvider<CodegenTask> {
        val capitalizedSourceName = sourceSetName.replaceFirstChar { it.uppercase() }
        return project.tasks.register<CodegenTask>("codegenModule$capitalizedSourceName${module.name}") {
            group = "chasm"
            description = "Generates a typesafe Kotlin interface from a wasm binary"

            workerClasspath.from(classpath)

            moduleBinary?.let {
                binary.set(moduleBinary)
            } ?: binary.set(module.binary)
            allocator.set(module.allocator)
            config.set(module.codegenConfig)
            interfaceName.set(module.name)
            packageName.set(module.packageName)
            interfaceVisibility.set(module.interfaceVisibility)
            implementationVisibility.set(module.implementationVisibility)
            initializers.set(module.initializers)
            functions.set(module.functions)
            ignoredExports.set(module.ignoredExports)
            outputDirectory.set(project.layout.buildDirectory.dir("generated/kotlin/$sourceSetName"))
        }
    }

    private fun artifactSuffixFor(target: KotlinTarget): String? = when (target.platformType) {
        KotlinPlatformType.jvm -> "jvm"
        KotlinPlatformType.js -> "js"
        KotlinPlatformType.androidJvm -> "android"
        KotlinPlatformType.wasm -> null
        KotlinPlatformType.native -> {
            val kn = (target as KotlinNativeTarget).konanTarget.name
            kn.lowercase().replace("_", "")
        }
        else -> null
    }

    private fun KotlinTarget.addVMRuntimeToKmpTarget(
        configuration: RuntimeDependencyConfiguration,
    ) {
        val suffix = artifactSuffixFor(this) ?: return
        val notation = resolveVMRuntimeNotation(suffix)

        val compilation = compilations.getByName("main")
        val configurationName = when (configuration) {
            RuntimeDependencyConfiguration.API -> compilation.defaultSourceSet.apiConfigurationName
            RuntimeDependencyConfiguration.IMPLEMENTATION -> compilation.defaultSourceSet.implementationConfigurationName
        }

        val exists = project.configurations.getByName(configurationName).dependencies.any {
            it.group == RUNTIME_GROUP && it.name == "$RUNTIME_ARTIFACT-$suffix"
        }
        if (!exists) {
            project.dependencies.add(configurationName, notation)
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
        private const val WORKER_CLASSPATH_CONFIGURATION_NAME = "chasmCodegenWorkerClasspath"
    }
}
