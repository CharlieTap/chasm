package io.github.charlietap.chasm.corpus

import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusRunner
import io.github.charlietap.corpus.lib.CorpusSettings

expect class PlatformCorpusRunner(
    version: String,
    path: String,
    settings: CorpusSettings,
) : CorpusRunner {

    override fun run(): CorpusResult
}
