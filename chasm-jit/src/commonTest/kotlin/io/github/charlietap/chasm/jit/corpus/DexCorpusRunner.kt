package io.github.charlietap.chasm.jit.corpus

import io.github.charlietap.chasm.compiled.corpus.CompiledCorpusRunner
import io.github.charlietap.chasm.jit.onLargeStack
import io.github.charlietap.chasm.jit.script.compileDex
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture

class DexCorpusRunner(
    fileReader: CorpusFileReader,
) : CorpusRunner {

    private val runner = CompiledCorpusRunner(fileReader, ::compileDex)

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
    ): CorpusExecution = onLargeStack { runner.execute(corpusRoot, fixture, phase) }
}
