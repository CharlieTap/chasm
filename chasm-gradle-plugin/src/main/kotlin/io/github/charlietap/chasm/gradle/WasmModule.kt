package io.github.charlietap.chasm.gradle

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
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

        init {
            binary.convention(project.layout.projectDirectory.file(DEFAULT_MODULE_FILE_PATH))
            codegenConfig.convention(CodegenConfig())
        }

        private companion object {
            const val DEFAULT_MODULE_FILE_PATH = "src/main/wasm/module.wasm"
        }
    }
