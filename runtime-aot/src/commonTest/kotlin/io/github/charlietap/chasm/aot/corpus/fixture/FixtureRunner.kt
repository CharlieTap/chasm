package io.github.charlietap.chasm.aot.corpus.fixture

import io.github.charlietap.chasm.aot.corpus.failure
import io.github.charlietap.chasm.aot.script.ScriptModules
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult

internal typealias FixtureRunner = (FixtureContext, ByteArray) -> CorpusResult

internal fun FixtureRunner(
    context: FixtureContext,
    bytes: ByteArray,
): CorpusResult = FixtureRunner(
    context = context,
    bytes = bytes,
    testRunner = ::TestRunner,
)

private fun FixtureRunner(
    context: FixtureContext,
    bytes: ByteArray,
    testRunner: TestRunner,
): CorpusResult {
    val fixture = context.fixture
    val timings = context.timings
    val moduleConfig = context.config.moduleConfig

    val decoded = when (val result = timings.decode { module(bytes, moduleConfig) }) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return fixture.failure("decode", "failed to decode wasm", result.error.toString())
    }
    if (context.phase == CorpusPhase.DECODING) return CorpusResult.Success

    val compiled = when (val result = timings.validate { ScriptModules.compile(bytes, moduleConfig, StoreConfig()) }) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return fixture.failure("validate", "failed to validate wasm", result.error.toString())
    }
    if (context.phase == CorpusPhase.VALIDATION) return CorpusResult.Success

    val module = PreparedModule(decoded, compiled)
    if (fixture.tests.isEmpty()) return testRunner(context, module, null)

    fixture.tests.withIndex().forEach { test ->
        val result = testRunner(context, module, test)
        if (result != CorpusResult.Success) return result
    }
    return CorpusResult.Success
}
