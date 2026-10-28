package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.RuntimeConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

internal fun compilerBaselineWriter(
    importResolver: CompilerBaselineImportResolver,
    runtimeConfig: RuntimeConfig = RuntimeConfig(),
    stores: List<CompilerBaselineStore> = listOf(CompilerBaselineStore.DEFAULT),
): CompilerBaselineWriter = CompilerBaselineWriter(
    generator = ModuleCompilerBaselineGenerator(
        importResolver = importResolver,
        moduleDecoder = ChasmModuleDecoder(),
        moduleInstantiator = ChasmModuleInstantiator(),
        runtimeConfig = runtimeConfig,
        stores = stores,
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

@OptIn(ExperimentalSerializationApi::class)
internal val cirBaselineJson = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}
