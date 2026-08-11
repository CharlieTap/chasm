package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.RuntimeConfig
import kotlinx.serialization.json.Json

internal fun compilerBaselineWriter(
    importResolver: CompilerBaselineImportResolver,
    runtimeConfig: RuntimeConfig = RuntimeConfig(),
): CompilerBaselineWriter = CompilerBaselineWriter(
    generator = ModuleCompilerBaselineGenerator(
        importResolver = importResolver,
        moduleDecoder = ChasmModuleDecoder(),
        moduleInstantiator = ChasmModuleInstantiator(),
        instructionCollectorFactory = DefaultProgramInstructionCollectorFactory(
            tagTranslator = CompilerInstructionTagTranslator(),
        ),
        runtimeConfig = runtimeConfig,
    ),
    json = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    },
)
