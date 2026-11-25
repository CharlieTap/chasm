package io.github.charlietap.corpus.plugin.spec

import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusSettings
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CorpusTestFileSpecTest {

    @Test
    fun `generates one test method per fixture with the settings baked in`() {

        val fixtures = listOf(
            GeneratedFixture("activity_selection", "1.0", "activity_selection.wasm"),
            GeneratedFixture("sqlite3", "2.0", "c/sqlite3.wasm"),
        )

        val generated = corpusTestFileSpec(PACKAGE, RUNNER, settings(), fixtures).toString()

        assertEquals(GOLDEN, generated)
    }

    @Test
    fun `replaces characters a method name cannot hold and prefixes a leading digit`() {

        val fixtures = listOf(
            GeneratedFixture("7zip-decompress.v2", "1.0", "7zip.wasm"),
        )

        val generated = corpusTestFileSpec(PACKAGE, RUNNER, settings(), fixtures).toString()

        assertContains(generated, "fun _7zip_decompress_v2()")
    }

    @Test
    fun `fails when two fixtures sanitise to the same method name`() {

        val fixtures = listOf(
            GeneratedFixture("hello-world", "1.0", "hello-world.wasm"),
            GeneratedFixture("hello_world", "2.0", "hello_world.wasm"),
        )

        val failure = assertFailsWith<IllegalArgumentException> {
            corpusTestFileSpec(PACKAGE, RUNNER, settings(), fixtures)
        }

        assertEquals(
            "Corpus fixtures generate the same test method: hello-world, hello_world -> hello_world",
            failure.message,
        )
    }

    private fun settings(): CorpusSettings {
        return CorpusSettings(
            phase = CorpusPhase.INVOCATION,
            engines = linkedSetOf("interpreter", "compiled"),
            warmups = 3,
            rounds = 5,
            pin = true,
        )
    }

    private companion object {
        const val PACKAGE = "io.github.charlietap.chasm.corpus.generated"
        const val RUNNER = "io.github.charlietap.chasm.corpus.PlatformCorpusRunner"

        val GOLDEN = """
            |package io.github.charlietap.chasm.corpus.generated
            |
            |import io.github.charlietap.chasm.corpus.PlatformCorpusRunner
            |import io.github.charlietap.corpus.lib.CorpusPhase
            |import io.github.charlietap.corpus.lib.CorpusResult
            |import io.github.charlietap.corpus.lib.CorpusSettings
            |import kotlin.String
            |import kotlin.test.Test
            |import kotlin.test.assertTrue
            |
            |public class CorpusTest {
            |    @Test
            |    public fun activity_selection() {
            |        run("1.0", "activity_selection.wasm")
            |    }
            |
            |    @Test
            |    public fun sqlite3() {
            |        run("2.0", "c/sqlite3.wasm")
            |    }
            |
            |    private fun run(version: String, path: String) {
            |        val result = PlatformCorpusRunner(version, path, settings).run()
            |        assertTrue(result !is CorpusResult.Failure, result.toString())
            |    }
            |
            |    private companion object {
            |        public val settings: CorpusSettings = CorpusSettings(
            |                phase = CorpusPhase.INVOCATION,
            |                engines = setOf("interpreter", "compiled"),
            |                warmups = 3,
            |                rounds = 5,
            |                pin = true,
            |                )
            |    }
            |}
            |
        """.trimMargin()
    }
}
