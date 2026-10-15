package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.RuntimeConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

internal fun compilerBaselineWriter(
    importResolver: CompilerBaselineImportResolver,
    runtimeConfig: RuntimeConfig = RuntimeConfig(),
): CompilerBaselineWriter = CompilerBaselineWriter(
    generator = ModuleCompilerBaselineGenerator(
        importResolver = importResolver,
        moduleDecoder = ChasmModuleDecoder(),
        moduleInstantiator = ChasmModuleInstantiator(),
        instructionCollectorFactory = ::IdentityProgramInstructionCollector,
        tagTranslator = CompilerInstructionTagTranslator(),
        runtimeConfig = runtimeConfig,
    ),
    json = compilerBaselineJson,
)

@OptIn(ExperimentalSerializationApi::class)
internal val compilerBaselineJson = Json {
    prettyPrint = true
    prettyPrintIndent = "    "
    explicitNulls = false
    namingStrategy = JsonNamingStrategy.SnakeCase
}
