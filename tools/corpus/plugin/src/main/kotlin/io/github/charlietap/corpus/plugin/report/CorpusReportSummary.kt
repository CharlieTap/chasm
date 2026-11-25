package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Timings
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.lib.report.EngineSummary
import java.io.File

class CorpusReportSummary {

    fun describe(
        report: CorpusReport,
        file: File,
    ): String {

        val engines = report.summary.map { summary -> describeEngine(summary) }
        val header = "Corpus report for ${report.environment.name}: ${file.absolutePath}"

        return (listOf(header) + engines).joinToString("\n")
    }

    private fun describeEngine(summary: EngineSummary): String {

        val counts = "  ${summary.engine}: ${summary.passed} passed, ${summary.skipped} skipped, ${summary.failed} failed, " +
            "${summary.missing.size} missing"
        val missing = if (summary.missing.isEmpty()) {
            emptyList()
        } else {
            listOf("    missing: ${summary.missing.joinToString()}")
        }
        val cold = listOf("    cold: ${describeTimings(summary.cold)}")
        val warm = summary.warm?.let { timings -> listOf("    warm: ${describeTimings(timings)}") }.orEmpty()

        return (listOf(counts) + missing + cold + warm).joinToString("\n")
    }

    private fun describeTimings(timings: Timings): String {

        val instantiation = milliseconds(timings.moduleNanos + timings.instantiateNanos)
        val execution = milliseconds(timings.executeNanos)

        return "instantiation $instantiation ms, execution $execution ms"
    }

    private fun milliseconds(nanos: Long): String {
        return "%.1f".format(nanos / NANOS_PER_MILLISECOND)
    }

    private companion object {
        const val NANOS_PER_MILLISECOND = 1_000_000.0
    }
}
