package io.github.charlietap.corpus.lib

data class CorpusSettings(
    val phase: CorpusPhase,
    val engines: Set<String>,
    val warmups: Int,
    val rounds: Int,
    val pin: Boolean,
)
