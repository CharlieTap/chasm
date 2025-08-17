package io.github.charlietap.chasm.gradle

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

@ChasmDsl
abstract class WasmModule
    @Inject
    constructor(
        val project: Project,
        val name: String,
    ) {
        abstract val binary: RegularFileProperty
        abstract val codegenConfig: Property<CodegenConfig>
        abstract val packageName: Property<String>
        abstract val initializers: SetProperty<String>
        abstract val functions: ListProperty<WasmFunction>
        abstract val interfaceVisibility: Property<TypeVisibility>
        abstract val implementationVisibility: Property<TypeVisibility>
        abstract val ignoredExports: SetProperty<String>

        init {
            binary.convention(project.layout.projectDirectory.file(DEFAULT_MODULE_FILE_PATH))
            codegenConfig.convention(CodegenConfig())
            functions.convention(emptyList())
            interfaceVisibility.convention(TypeVisibility.PUBLIC)
            implementationVisibility.convention(TypeVisibility.INTERNAL)
        }

        fun function(name: String, configuration: WasmFunctionBuilder.() -> Unit) {
            val function = WasmFunctionBuilder(name).apply {
                configuration()
            }.build()
            functions.add(function)
        }

        private companion object {
            const val DEFAULT_MODULE_FILE_PATH = "src/main/wasm/module.wasm"
        }
    }
