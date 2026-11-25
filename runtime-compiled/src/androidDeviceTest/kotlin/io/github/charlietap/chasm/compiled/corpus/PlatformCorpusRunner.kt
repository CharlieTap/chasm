package io.github.charlietap.chasm.compiled.corpus

import io.github.charlietap.chasm.compiled.onLargeStack
import io.github.charlietap.chasm.compiled.script.compileDexModule
import io.github.charlietap.corpus.lib.CorpusExecution
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.fixture.Fixture

class PlatformCorpusRunner(
    fileReader: CorpusFileReader,
) : CorpusRunner {

    private val runner = CompiledCorpusRunner(fileReader, ::compileDexModule)

    override fun readText(path: String): String {
        return runner.readText(path)
    }

    override fun readBytes(path: String): ByteArray {
        return runner.readBytes(path)
    }

    override fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusExecution {
        return onLargeStack {
            runner.execute(corpusRoot, fixture, phase)
        }
    }
}
