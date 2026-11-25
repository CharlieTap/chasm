package io.github.charlietap.corpus.plugin.device

import org.gradle.api.GradleException
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApkCorpusFilesTest {

    private val directory = createTempDirectory("apk-corpus-files").toFile()

    @AfterTest
    fun deleteDirectory() {
        directory.deleteRecursively()
    }

    @Test
    fun `reads the run file and fixture index wherever the apk places the corpus folder`() {

        val apk = apk(
            "classes.dex" to "dex",
            "resources/corpus/run.json" to RUN,
            "resources/corpus/fixtures.json" to FIXTURES,
            "resources/corpus/1.0/fixtures.json" to "a fixture named fixtures",
        )

        val files = ApkCorpusFiles(apk)

        assertEquals(RUN, files.runJson())
        assertEquals(FIXTURES, files.fixturesIndexJson())
    }

    @Test
    fun `reads the corpus folder at the root of the apk`() {

        val apk = apk(
            "corpus/run.json" to RUN,
            "corpus/fixtures.json" to FIXTURES,
        )

        val files = ApkCorpusFiles(apk)

        assertEquals(RUN, files.runJson())
        assertEquals(FIXTURES, files.fixturesIndexJson())
    }

    @Test
    fun `fails for an apk the corpus plugin did not build`() {

        val apk = apk("classes.dex" to "dex")

        val failure = assertFailsWith<GradleException> {
            ApkCorpusFiles(apk).runJson()
        }

        assertEquals("test.apk must hold exactly one corpus/run.json, found 0; build it with the corpus plugin", failure.message)
    }

    @Test
    fun `fails when the apk holds the run file twice`() {

        val apk = apk(
            "a/corpus/run.json" to RUN,
            "b/corpus/run.json" to RUN,
        )

        val failure = assertFailsWith<GradleException> {
            ApkCorpusFiles(apk).runJson()
        }

        assertEquals("test.apk must hold exactly one corpus/run.json, found 2; build it with the corpus plugin", failure.message)
    }

    private fun apk(vararg entries: Pair<String, String>): File {

        val apk = directory.resolve("test.apk")

        ZipOutputStream(apk.outputStream()).use { zip ->
            entries.forEach { (name, content) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(content.encodeToByteArray())
                zip.closeEntry()
            }
        }

        return apk
    }

    private companion object {
        const val RUN = """{"phase":"invocation","engines":["compiled"]}"""
        const val FIXTURES = """[{"name":"activity_selection","version":"1.0","path":"activity_selection.wasm"}]"""
    }
}
