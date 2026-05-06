package io.github.charlietap.corpus.lib

import io.github.charlietap.corpus.lib.fixture.Fixture

interface CorpusRunner {
    fun readText(path: String): String

    fun readBytes(path: String): ByteArray

    fun execute(
        corpusRoot: String,
        fixture: Fixture,
        phase: CorpusPhase,
    ): CorpusResult
}
