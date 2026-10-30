package io.github.charlietap.chasm.aot.corpus

import io.github.charlietap.chasm.aot.corpus.fixture.FixtureContext
import io.github.charlietap.chasm.aot.corpus.fixture.FixtureRunner
import io.github.charlietap.chasm.aot.script.ScriptModules
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlin.time.TimeSource

class AotCorpusRunner(
    private val fileReader: CorpusFileReader,
    private val config: Config = Config(
        runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    ),
) : CorpusRunner {

    override fun readText(
        path: String,
    ): String = fileReader.readText(path)

    override fun readBytes(
        path: String,
    ): ByteArray = fileReader.readBytes(path)

    override fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusExecution {
        ScriptModules.useTestStack()
        val timings = CorpusTimingRecorder()
        val start = TimeSource.Monotonic.markNow()
        val bytes = readBytes("$corpusRoot/${fixture.version ?: "1.0"}/${fixture.path}")
        timings.binarySizeBytes = bytes.size.toLong()
        val result = FixtureRunner(FixtureContext(fixture, phase, config, timings), bytes)
        return CorpusExecution(
            result = result,
            timings = timings.snapshot(start.elapsedNow().inWholeNanoseconds),
            binarySizeBytes = timings.binarySizeBytes,
            moduleBuildCount = timings.moduleBuildCount,
        )
    }
}
