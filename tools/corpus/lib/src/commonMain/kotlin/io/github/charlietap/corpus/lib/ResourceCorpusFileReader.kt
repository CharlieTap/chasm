package io.github.charlietap.corpus.lib

import com.goncalossilva.resources.Resource

class ResourceCorpusFileReader : CorpusFileReader {
    override fun readText(path: String): String = Resource(path).readText()

    override fun readBytes(path: String): ByteArray = Resource(path).readBytes()
}
