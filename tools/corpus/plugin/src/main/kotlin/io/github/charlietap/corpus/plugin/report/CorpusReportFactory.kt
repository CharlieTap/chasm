package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.lib.report.EngineSummary

class CorpusReportFactory {

    fun create(
        environment: CorpusEnvironment,
        environmentEngines: Set<String>,
        configuration: CorpusConfiguration,
        fixtures: List<String>,
        measurements: List<Measurement>,
    ): CorpusReport {

        val sorted = measurements.sortedWith(compareBy(Measurement::engine, Measurement::version, Measurement::fixture))
        val summary = expectedEngines(configuration, environmentEngines).map { engine ->
            summary(engine, fixtures, sorted.filter { measurement -> measurement.engine == engine })
        }

        return CorpusReport(SCHEMA_VERSION, environment, configuration, summary, sorted)
    }

    private fun expectedEngines(
        configuration: CorpusConfiguration,
        environmentEngines: Set<String>,
    ): List<String> {
        return if (configuration.phase in FRONT_END_PHASES) {
            listOf(FRONT_END)
        } else {
            configuration.engines.filter { engine -> engine in environmentEngines }
        }
    }

    private fun summary(
        engine: String,
        fixtures: List<String>,
        measurements: List<Measurement>,
    ): EngineSummary {

        val passed = measurements.filter { measurement -> measurement.outcome == Outcome.Passed }
        val measuredFixtures = measurements.map(Measurement::fixture).toSet()
        val warm = passed.mapNotNull(Measurement::warm)
        val warmTotal = if (warm.size == passed.size && warm.isNotEmpty()) {
            total(warm)
        } else {
            null
        }

        return EngineSummary(
            engine = engine,
            passed = passed.size,
            skipped = measurements.count { measurement -> measurement.outcome == Outcome.Skipped },
            failed = measurements.count { measurement -> measurement.outcome == Outcome.Failed },
            missing = fixtures.filterNot { fixture -> fixture in measuredFixtures },
            cold = total(passed.map(Measurement::cold)),
            warm = warmTotal,
        )
    }

    private fun total(timings: List<Timings>): Timings {
        return Timings(
            totalNanos = timings.sumOf { timing -> timing.totalNanos },
            decodeNanos = timings.sumOf { timing -> timing.decodeNanos },
            validateNanos = timings.sumOf { timing -> timing.validateNanos },
            moduleNanos = timings.sumOf { timing -> timing.moduleNanos },
            instantiateNanos = timings.sumOf { timing -> timing.instantiateNanos },
            executeNanos = timings.sumOf { timing -> timing.executeNanos },
        )
    }

    private companion object {
        const val SCHEMA_VERSION = 2
        const val FRONT_END = "frontend"
        val FRONT_END_PHASES = setOf("decoding", "validation")
    }
}
