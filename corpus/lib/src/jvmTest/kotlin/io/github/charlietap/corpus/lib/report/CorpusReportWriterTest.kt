package io.github.charlietap.corpus.lib.report

import kotlinx.serialization.json.Json
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals

class CorpusReportWriterTest {

    @Test
    fun `writes a fixture result to the injected directory`() {
        val resultsDirectory = createTempDirectory("corpus-results")
        val result = fixtureResult()

        try {
            CorpusReportWriter().write(resultsDirectory, result)

            val outputFile = resultsDirectory.resolve("2.0").resolve("example.json")
            assertEquals(
                result,
                Json.decodeFromString<CorpusFixtureResult>(outputFile.readText()),
            )
        } finally {
            Files.walk(resultsDirectory).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        }
    }

    private fun fixtureResult() = CorpusFixtureResult(
        version = "2.0",
        fixture = "example",
        path = "example.wasm",
        sha256 = "hash",
        phase = "decoding",
        outcome = "passed",
        detail = null,
        binarySizeBytes = 128,
        testCount = 1,
        stepCount = 2,
        moduleBuildCount = 0,
        totalNanos = 20,
        decodeNanos = 10,
        validateNanos = 0,
        instantiateNanos = 0,
        executeNanos = 0,
    )
}
