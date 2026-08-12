package io.github.charlietap.corpus.lib

interface CorpusFileReader {
    fun readText(path: String): String

    fun readBytes(path: String): ByteArray
}
