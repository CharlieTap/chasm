package io.github.charlietap.chasm.compiled.corpus.fixture

import io.github.charlietap.chasm.compiled.corpus.failure
import io.github.charlietap.chasm.compiled.script.ScriptModules
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

    val decoded = timings.decode { ScriptModules.decode(bytes, moduleConfig) }
    if (decoded is ChasmResult.Error) return fixture.failure("decode", "failed to decode wasm", decoded.error.toString())
    if (context.phase == CorpusPhase.DECODING) return CorpusResult.Success

    val module = when (val result = timings.validate { context.compiler(bytes, moduleConfig, context.storeConfig) }) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return fixture.failure("validate", "failed to validate wasm", result.error.toString())
    }
    if (context.phase == CorpusPhase.VALIDATION) return CorpusResult.Success

    if (fixture.tests.isEmpty()) return testRunner(context, module, null)

    fixture.tests.withIndex().forEach { test ->
        val result = testRunner(context, module, test)
        if (result != CorpusResult.Success) return result
    }
    return CorpusResult.Success
}
