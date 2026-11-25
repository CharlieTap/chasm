package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.plugin.report.BaselineNames
import io.github.charlietap.corpus.plugin.report.ComparisonSummary
import io.github.charlietap.corpus.plugin.report.ReportComparison
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Prints a comparison")
abstract class CompareCorpusReportsTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val before: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val after: ConfigurableFileCollection

    @get:Internal
    abstract val baselineDirectory: DirectoryProperty

    @get:Input
    abstract val noisyTargets: ListProperty<String>

    @TaskAction
    fun compare() {

        val afterFiles = after.files.toList()
        if (warnIfMissing(afterFiles)) {
            return
        }

        val afterReports = afterFiles.map { file -> readReport(file) }
        val beforeFiles = before.files.toList().ifEmpty { listOf(baselineFile(afterReports.first())) }
        if (warnIfMissing(beforeFiles)) {
            return
        }

        val comparison = ReportComparison(noisyTargets.get().toSet()).compare(
            before = beforeFiles.map { file -> readReport(file) },
            after = afterReports,
        )

        logger.lifecycle(ComparisonSummary().describe(comparison, beforeFiles, afterFiles))
    }

    private fun warnIfMissing(files: List<File>): Boolean {

        val missing = files.filterNot(File::exists)

        if (files.isEmpty()) {
            logger.warn("No corpus report to compare")
        }
        if (missing.isNotEmpty()) {
            logger.warn("No corpus report at ${missing.joinToString { file -> file.absolutePath }}")
        }

        return files.isEmpty() || missing.isNotEmpty()
    }

    private fun readReport(file: File): CorpusReport {
        return Json { ignoreUnknownKeys = true }.decodeFromString(CorpusReport.serializer(), file.readText())
    }

    private fun baselineFile(report: CorpusReport): File {
        return baselineDirectory.file(BaselineNames().name(report.environment)).get().asFile
    }
}
