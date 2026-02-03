package io.github.charlietap.chasm.gradle

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.ClassLoaderWorkerSpec
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class CodegenTask
    @Inject
    constructor(
        private val workerExecutor: WorkerExecutor,
    ) : DefaultTask() {

        @get:Classpath
        abstract val workerClasspath: ConfigurableFileCollection

        @get:InputFile
        @get:PathSensitive(PathSensitivity.RELATIVE)
        abstract val binary: RegularFileProperty

        @get:Optional
        @get:Input
        abstract val allocator: Property<ExportedAllocator>

        @get:Input
        abstract val config: Property<CodegenConfig>

        @get:Input
        abstract val interfaceName: Property<String>

        @get:Input
        abstract val packageName: Property<String>

        @get:Input
        abstract val interfaceVisibility: Property<TypeVisibility>

        @get:Input
        abstract val implementationVisibility: Property<TypeVisibility>

        @get:Input
        abstract val initializers: SetProperty<String>

        @get:Input
        abstract val functions: ListProperty<WasmFunction>

        @get:Input
        abstract val ignoredExports: SetProperty<String>

        @get:OutputDirectory
        abstract val outputDirectory: DirectoryProperty

        @TaskAction
        fun generate() {
            val binaryFile = binary.get().asFile
            val outputDir = outputDirectory.get().asFile
            val interfaceNameValue = interfaceName.get()
            val packageNameValue = packageName.get()
            val interfaceVisibilityValue = interfaceVisibility.get()
            val implementationVisibilityValue = implementationVisibility.get()
            val codegenConfigValue = config.get()
            val allocatorValue = allocator.orNull
            val initializerNames = initializers.get()
            val wasmFunctions = functions.get()
            val ignoredExportNames = ignoredExports.get()

            val workQueue = workerExecutor.classLoaderIsolation {
                classpath.from(workerClasspath)
            }

            workQueue.submit(CodegenWorkAction::class.java) {

                binaryPath.set(binaryFile.absolutePath)
                outputDirectoryPath.set(outputDir.absolutePath)
                interfaceName.set(interfaceNameValue)
                packageName.set(packageNameValue)
                interfaceVisibility.set(interfaceVisibilityValue.name)
                implementationVisibility.set(implementationVisibilityValue.name)

                configGenerateTypesafeGlobalProperties.set(codegenConfigValue.generateTypesafeGlobalProperties)

                hasAllocator.set(allocatorValue != null)
                allocatorValue?.let {
                    allocatorAllocationFunction.set(it.allocationFunction)
                    allocatorDeallocationFunction.set(it.deallocationFunction)
                }

                initializers.set(initializerNames)
                functions.set(wasmFunctions.map { it.toWorkData() })
                ignoredExports.set(ignoredExportNames)
            }
        }
    }
