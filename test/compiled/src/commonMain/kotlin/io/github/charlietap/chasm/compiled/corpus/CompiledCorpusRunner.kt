package io.github.charlietap.chasm.compiled.corpus

import io.github.charlietap.chasm.compiled.Compiler
import io.github.charlietap.chasm.compiled.corpus.fixture.FixtureContext
import io.github.charlietap.chasm.compiled.corpus.fixture.FixtureRunner
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlin.time.TimeSource

class CompiledCorpusRunner(
    private val fileReader: CorpusFileReader,
    private val compiler: Compiler,
    private val config: Config = Config(),
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
        val timings = CorpusTimingRecorder()
        val start = TimeSource.Monotonic.markNow()
        val bytes = readBytes("$corpusRoot/${fixture.version ?: "1.0"}/${fixture.path}")
        timings.binarySizeBytes = bytes.size.toLong()
        val result = FixtureRunner(FixtureContext(fixture, phase, config, timings, compiler), bytes)
        return CorpusExecution(
            result = result,
            timings = timings.snapshot(start.elapsedNow().inWholeNanoseconds),
            binarySizeBytes = timings.binarySizeBytes,
            moduleBuildCount = timings.moduleBuildCount,
        )
    }
}
