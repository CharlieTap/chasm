package io.github.charlietap.chasm.compiled.corpus.instance

import at.released.weh.host.EmbedderHost
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.compiled.corpus.failure
import io.github.charlietap.chasm.compiled.corpus.fixture.FixtureContext
import io.github.charlietap.chasm.compiled.corpus.imports.ImportResolver
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.FixtureTest
import kotlinx.coroutines.runBlocking

internal typealias ModuleInstantiator = (FixtureContext, Store, Module, FixtureTest?) -> Result<RuntimeSetup, CorpusResult>

internal fun ModuleInstantiator(
    context: FixtureContext,
    store: Store,
    module: Module,
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
    module: Module,
    test: FixtureTest?,
    importResolver: ImportResolver,
): Result<RuntimeSetup, CorpusResult> {
    val imports = importResolver(context.fixture, store, module, test, context.timings).getOrElse { error -> return Err(error) }

    return runBlocking { instance(store, module, imports.imports) }.fold(
        { instance ->
            val exports = exports(instance)
            val exportedMemory = exports.all.firstNotNullOfOrNull { export -> export.value as? Memory }
            if (imports.memories.isEmpty() && exportedMemory != null) {
                imports.memories["memory"] = exportedMemory
            }
            Ok(RuntimeSetup(instance, exports, imports))
        },
        { error ->
            imports.hosts.forEach(EmbedderHost::close)
            Err(context.fixture.failure("instantiate", "failed to instantiate wasm", error.toString()))
        },
    )
}
