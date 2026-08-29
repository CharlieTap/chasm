package io.github.charlietap.chasm.gradle

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
            val workQueue = workerExecutor.classLoaderIsolation { spec: ClassLoaderWorkerSpec ->
                spec.classpath.from(workerClasspath)
            }

            workQueue.submit(CodegenWorkAction::class.java) { workParameters ->
                workParameters.binary.set(this@CodegenTask.binary)
                workParameters.outputDirectory.set(this@CodegenTask.outputDirectory)
                workParameters.interfaceName.set(this@CodegenTask.interfaceName)
                workParameters.packageName.set(this@CodegenTask.packageName)
                workParameters.interfaceVisibility.set(this@CodegenTask.interfaceVisibility)
                workParameters.implementationVisibility.set(this@CodegenTask.implementationVisibility)
                workParameters.config.set(this@CodegenTask.config)
                workParameters.allocator.set(this@CodegenTask.allocator)
                workParameters.initializers.set(this@CodegenTask.initializers)
                workParameters.functions.set(this@CodegenTask.functions)
                workParameters.ignoredExports.set(this@CodegenTask.ignoredExports)
            }
        }
    }
