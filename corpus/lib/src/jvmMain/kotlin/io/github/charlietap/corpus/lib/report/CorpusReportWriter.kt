package io.github.charlietap.corpus.lib.report

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class CorpusReportWriter(
    private val json: Json = Json { prettyPrint = true },
) {
    fun write(
        resultsDirectory: Path,
        result: CorpusFixtureResult,
    ) {
        val versionDirectory = resultsDirectory.resolve(result.version)
        Files.createDirectories(versionDirectory)

        val outputFile = versionDirectory.resolve("${result.fixture}.json")
        val temporaryFile = Files.createTempFile(versionDirectory, result.fixture, ".tmp")
        Files.writeString(temporaryFile, json.encodeToString(result))
        try {
            Files.move(
                temporaryFile,
                outputFile,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporaryFile, outputFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
