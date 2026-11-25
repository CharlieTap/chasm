package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import java.io.File
import java.util.zip.ZipFile

class ApkCorpusFiles(
    private val apk: File,
) {

    fun runJson(): String {
        return read(RUN_FILE)
    }

    fun fixturesIndexJson(): String {
        return read(FIXTURES_INDEX)
    }

    private fun read(path: String): String {
        return ZipFile(apk).use { zip ->

            val entries = zip.entries().asSequence()
                .filter { entry -> entry.name == path || entry.name.endsWith("/$path") }
                .toList()
            val entry = entries.singleOrNull() ?: throw GradleException(
                "${apk.name} must hold exactly one $path, found ${entries.size}; build it with the corpus plugin",
            )

            zip.getInputStream(entry).use { input -> input.readBytes().decodeToString() }
        }
    }

    private companion object {
        const val RUN_FILE = "corpus/run.json"
        const val FIXTURES_INDEX = "corpus/fixtures.json"
    }
}
