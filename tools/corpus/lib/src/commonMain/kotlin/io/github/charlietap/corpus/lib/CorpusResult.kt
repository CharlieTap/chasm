package io.github.charlietap.corpus.lib

sealed interface CorpusResult {
    data object Success : CorpusResult

    data class Failure(
        val fixture: String,
        val context: String,
        val message: String,
        val detail: String? = null,
        val expected: String? = null,
        val actual: String? = null,
        val rerun: String? = null,
    ) : CorpusResult

    data class Skipped(
        val fixture: String,
        val reason: String,
    ) : CorpusResult
}
