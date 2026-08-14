package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.chasm_gradle_plugin.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import kotlin.jvm.java

class ChasmPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val extension = project.extensions.create("chasm", ChasmExtension::class.java, project.objects)

        val workerClasspath = createWorkerClasspathConfiguration(project)

        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val mpp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            project.afterEvaluate {
                extension.modules.configureEach { module ->
                    when (extension.mode.get()) {
                        Mode.CONSUMER -> {
                            val commonMainSourceSet = mpp.sourceSets.getByName("commonMain")
                            addVMRuntimeForKmp(project, extension.runtimeDependencyConfiguration.get(), commonMainSourceSet)

                            val task = registerCodegenTask(
                                project,
                                module,
                                "commonMain",
                                workerClasspath,
                                extension.generateSuspendingFactories,
                            )
                            commonMainSourceSet.kotlin.srcDir(task.flatMap { it.outputDirectory })
                        }
                        Mode.PRODUCER -> {
                            configureProducerModule(project, mpp, extension, module, workerClasspath)
                        }
                    }
                }
            }
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            val jvmExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val mainCompilation = jvmExtension.target.compilations.getByName(MAIN_COMPILATION_NAME)

            addVMRuntimeForJvmOrAndroid(project, extension.runtimeDependencyConfiguration.get())

            project.afterEvaluate {
                extension.modules.configureEach { module ->
                    if (extension.mode.get() == Mode.PRODUCER) {
                        project.logger.error("Producer mode is only supported for Kotlin Multiplatform projects with WASM targets")
                        return@configureEach
                    }

                    val task = registerCodegenTask(
                        project,
                        module,
                        MAIN_COMPILATION_NAME,
                        workerClasspath,
                        extension.generateSuspendingFactories,
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

    private fun configureProducerModule(
        project: Project,
        mpp: KotlinMultiplatformExtension,
        extension: ChasmExtension,
        module: WasmModule,
        workerClasspath: Configuration,
    ) {
        project.logger.warn(
            "Producer mode is deprecated and will be removed in a future release. " +
                "For a more robust solution, see https://github.com/CharlieTap/glueball",
        )

        val wasmTargets = mpp.targets.withType(KotlinJsIrTarget::class.java).filter { target ->
            target.platformType == KotlinPlatformType.wasm
        }
        if (wasmTargets.isEmpty()) {
            throw GradleException("Producer mode requires at least one WASM target (wasmJs or wasmWasi)")
        }

        val generatedSources = project.objects.fileCollection()
        val generatedResources = project.objects.fileCollection()

        mpp.targets.configureEach { target ->
            target.addVMRuntimeToKmpTarget(extension.runtimeDependencyConfiguration.get())

            if (target.platformType != KotlinPlatformType.wasm && target.platformType != KotlinPlatformType.common) {
                val mainCompilation = target.compilations.getByName(MAIN_COMPILATION_NAME)
                mainCompilation.defaultSourceSet.kotlin.srcDir(generatedSources)
                mainCompilation.defaultSourceSet.resources.srcDir(generatedResources)
            }
        }

        wasmTargets.forEach { target ->
            target.compilations.configureEach { compilation ->
                compilation.compileTaskProvider.configure { compileTask ->
                    compileTask.compilerOptions.freeCompilerArgs.add("-Xwasm-use-new-exception-proposal")
                }
            }

            val executable = target
            val linkedBinary = executable.binaries.first()
            val wasmFile = project.layout.file(
                linkedBinary.mainFile.map { mainFile ->
                    val file = mainFile.asFile
                    file.resolveSibling(file.nameWithoutExtension + ".wasm")
                },
            )
            val codegen = registerCodegenTask(
                project,
                module,
                target.name,
                workerClasspath,
                extension.generateSuspendingFactories,
            ).apply {
                configure { task ->
                    task.binary.set(wasmFile)
                    task.dependsOn(linkedBinary.linkTask)
                }
            }
            val preparedResources = project.tasks.register(
                "prepareModule${target.name.toTaskNameSegment()}${module.name}Resources",
                Sync::class.java,
            ) { task ->
                task.dependsOn(linkedBinary.linkTask)
                task.from(wasmFile) { spec ->
                    spec.rename { "producer.wasm" }
                }
                task.into(project.layout.buildDirectory.dir("generated/resources/${target.name}/${module.name}"))
            }

            generatedSources.from(codegen.flatMap { it.outputDirectory })
            generatedResources.from(preparedResources.map { it.destinationDir })
        }
    }

    private fun String.toTaskNameSegment(): String = replaceFirstChar { it.uppercase() }

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
        private const val WORKER_DEPENDENCIES_CONFIGURATION_NAME = "chasmCodegenWorkerDependencies"
        private const val WORKER_CLASSPATH_CONFIGURATION_NAME = "chasmCodegenWorkerClasspath"
    }
}
