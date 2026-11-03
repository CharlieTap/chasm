package io.github.charlietap.chasm.aot.corpus

import io.github.charlietap.chasm.aot.script.AotCompiler
import io.github.charlietap.chasm.compiled.corpus.CompiledCorpusRunner
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture

class AotCorpusRunner(
    fileReader: CorpusFileReader,
) : CorpusRunner {

    private val runner = CompiledCorpusRunner(fileReader, AotCompiler::compile)

    override fun readText(
        path: String,
    ): String = runner.readText(path)

    override fun readBytes(
        path: String,
    ): ByteArray = runner.readBytes(path)

    override fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusExecution {
        AotCompiler.useTestStack()
        return runner.execute(corpusRoot, fixture, phase)
    }
}
