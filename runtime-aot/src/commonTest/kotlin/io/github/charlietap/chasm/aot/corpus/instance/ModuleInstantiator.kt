package io.github.charlietap.chasm.aot.corpus.instance

import at.released.weh.host.EmbedderHost
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.aot.corpus.failure
import io.github.charlietap.chasm.aot.corpus.fixture.FixtureContext
import io.github.charlietap.chasm.aot.corpus.fixture.PreparedModule
import io.github.charlietap.chasm.aot.corpus.imports.ImportResolver
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.FixtureTest

internal typealias ModuleInstantiator = (FixtureContext, Store, PreparedModule, FixtureTest?) -> Result<RuntimeSetup, CorpusResult>

internal fun ModuleInstantiator(
    context: FixtureContext,
    store: Store,
    module: PreparedModule,
    test: FixtureTest?,
): Result<RuntimeSetup, CorpusResult> = ModuleInstantiator(
    context = context,
    store = store,
    module = module,
    test = test,
    importResolver = ::ImportResolver,
)

private fun ModuleInstantiator(
    context: FixtureContext,
    store: Store,
    module: PreparedModule,
    test: FixtureTest?,
    importResolver: ImportResolver,
): Result<RuntimeSetup, CorpusResult> {
    val imports = importResolver(context.fixture, store, module.decoded, test).getOrElse { error -> return Err(error) }

    return instance(store, module.compiled, imports.imports, context.config.runtimeConfig).fold(
        { instance ->
            val exportedMemory = instance.exports.firstNotNullOfOrNull { export -> export.value as? Memory }
            if (imports.memories.isEmpty() && exportedMemory != null) {
                imports.memories["memory"] = exportedMemory
            }
            Ok(RuntimeSetup(store, instance, imports))
        },
        { error ->
            imports.hosts.forEach(EmbedderHost::close)
            Err(context.fixture.failure("instantiate", "failed to instantiate wasm", error.toString()))
        },
    )
}
