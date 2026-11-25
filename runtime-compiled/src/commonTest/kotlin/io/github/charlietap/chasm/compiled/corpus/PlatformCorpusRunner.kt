package io.github.charlietap.chasm.compiled.corpus

import io.github.charlietap.chasm.compiled.script.ClassFileCompiler
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture

class PlatformCorpusRunner(
    fileReader: CorpusFileReader,
) : CorpusRunner {

    private val runner = CompiledCorpusRunner(fileReader, ClassFileCompiler::compile)

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
        ClassFileCompiler.useTestStack()
        return runner.execute(corpusRoot, fixture, phase)
    }
}
