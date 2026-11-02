package io.github.charlietap.chasm.aot.corpus.imports

import at.released.weh.bindings.chasm.wasip1.ChasmWasiPreview1Builder
import at.released.weh.host.EmbedderHost
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.aot.corpus.value.defaultValue
import io.github.charlietap.chasm.aot.corpus.value.parseFixtureValue
import io.github.charlietap.chasm.aot.corpus.wasi.WasiHostFactory
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import io.github.charlietap.corpus.lib.fixture.FixtureTest

internal typealias ImportResolver = (Fixture, Store, Module, FixtureTest?) -> Result<ImportResolution, CorpusResult>

internal fun ImportResolver(
    fixture: Fixture,
    store: Store,
    module: Module,
    test: FixtureTest?,
): Result<ImportResolution, CorpusResult> = ImportResolver(
    fixture = fixture,
    store = store,
    module = module,
    test = test,
    wasiHostFactory = ::WasiHostFactory,
    stubFunctionFactory = ::StubFunctionFactory,
)

private fun ImportResolver(
    fixture: Fixture,
    store: Store,
    module: Module,
    test: FixtureTest?,
    wasiHostFactory: WasiHostFactory,
    stubFunctionFactory: StubFunctionFactory,
): Result<ImportResolution, CorpusResult> {
    val imports = mutableListOf<Import>()
    val memories = mutableMapOf<String, Memory>()
    val globals = mutableMapOf<String, Global>()
    val hosts = mutableListOf<EmbedderHost>()
    val stdout = mutableListOf<Byte>()
    val stderr = mutableListOf<Byte>()
    val captures = mutableMapOf<String, ImportCapture>()
    val hasWasiImports = module.imports.any { definition -> definition.moduleName in WASI_MODULES }

    val wasiHost = test?.host?.wasiPreview1
    if (wasiHost == null && hasWasiImports) {
        return Err(CorpusResult.Skipped(fixture.name, "WASI Preview 1 imports require fixture host config"))
    }

    if (wasiHost != null && hasWasiImports) {
        val host = wasiHostFactory(wasiHost, stdout, stderr)
        hosts += host
        imports += ChasmWasiPreview1Builder(store, module) {
            this.host = host
        }.build()
    }

    val provided = imports.map { import -> import.moduleName to import.entityName }.toMutableSet()

    module.imports.forEach { definition ->
        if (definition.moduleName in WASI_MODULES) return@forEach
        if ((definition.moduleName to definition.entityName) in provided) return@forEach

        val fixtureImport = fixture.findImport(definition)
        when (val type = definition.type) {
            is ExternalType.Function -> {
                val function = stubFunctionFactory(store, type.functionType, fixtureImport, captures)
                imports += Import(definition.moduleName, definition.entityName, function)
            }
            is ExternalType.Global -> {
                val value = fixtureImport?.stub?.value?.let(::parseFixtureValue) ?: defaultValue(type.globalType.valueType)
                val imported = global(store, type.globalType, value)
                globals[definition.key()] = imported
                imports += Import(definition.moduleName, definition.entityName, imported)
            }
            is ExternalType.Memory -> {
                val imported = memory(store, type.memoryType)
                memories[definition.key()] = imported
                imports += Import(definition.moduleName, definition.entityName, imported)
            }
            is ExternalType.Table -> return Err(
                CorpusResult.Skipped(fixture.name, "table imports are not supported yet: ${definition.key()}"),
            )
            is ExternalType.Tag -> return Err(
                CorpusResult.Skipped(fixture.name, "tag imports are not supported yet: ${definition.key()}"),
            )
        }
        provided += definition.moduleName to definition.entityName
    }

    return Ok(ImportResolution(imports, memories, globals, hosts, stdout, stderr, captures))
}

private fun Fixture.findImport(
    definition: ImportDefinition,
): FixtureImport? = imports.firstOrNull { import ->
    import.module == definition.moduleName && import.name == definition.entityName
}

private fun ImportDefinition.key(): String = "$moduleName.$entityName"

private val WASI_MODULES = setOf("wasi_snapshot_preview1", "wasi_unstable")
