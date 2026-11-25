package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.report.CorpusReport
import org.gradle.api.GradleException

class BaselineReportCheck(
    private val baselineTask: String,
    private val warmups: Int,
    private val rounds: Int,
    private val phase: String,
    private val environmentEngines: Set<String>,
) {

    fun check(report: CorpusReport) {

        val incomplete = report.summary.filter { summary -> summary.missing.isNotEmpty() || summary.failed > 0 }
        if (incomplete.isNotEmpty() || report.summary.isEmpty()) {
            throw GradleException("The report is incomplete: ${incomplete.map { summary -> summary.engine }}")
        }

        val configuration = report.configuration
        if (configuration.warmups != warmups || configuration.rounds != rounds) {
            throw GradleException(
                "A baseline is recorded with $warmups warm-ups and $rounds rounds, " +
                    "the report has ${configuration.warmups} and ${configuration.rounds}; " +
                    "request $baselineTask by its full name, abbreviated task names are not detected",
            )
        }
        if (configuration.targets.isNotEmpty()) {
            throw GradleException("A baseline covers every target, the report was narrowed to ${configuration.targets.joinToString()}")
        }
        if (configuration.phase != phase) {
            throw GradleException("A baseline is recorded for the $phase phase, the report measured ${configuration.phase}")
        }

        val engines = report.summary.map { summary -> summary.engine }.toSet()
        if (engines != environmentEngines) {
            throw GradleException(
                "A baseline covers every engine of the environment (${environmentEngines.sorted().joinToString()}), " +
                    "the report has ${engines.sorted().joinToString()}",
            )
        }
    }
}
