package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.lib.report.EngineSummary
import kotlin.math.exp
import kotlin.math.ln

class ReportComparison(
    private val noisyTargets: Set<String>,
) {

    fun compare(
        before: List<CorpusReport>,
        after: List<CorpusReport>,
    ): Comparison {

        val beforeEngines = engines(before)
        val engines = engines(after).filter { engine -> engine in beforeEngines }

        return Comparison(
            differences = differences(before + after),
            engines = engines.map { engine -> compareEngine(engine, before, after) },
        )
    }

    private fun engines(reports: List<CorpusReport>): List<String> {
        return reports.flatMap { report -> report.summary.map(EngineSummary::engine) }.distinct()
    }

    private fun differences(reports: List<CorpusReport>): List<Difference> {
        return COMPARED_PROPERTIES.map { (property, value) -> Difference(property, reports.map(value).distinct()) }
            .filter { difference -> difference.values.size > 1 }
    }

    private fun compareEngine(
        engine: String,
        before: List<CorpusReport>,
        after: List<CorpusReport>,
    ): EngineComparison {

        val metrics = Metric.entries.mapNotNull { metric ->
            compareMetric(metric, medians(before, engine, metric), medians(after, engine, metric))
        }

        return EngineComparison(engine, metrics)
    }

    private fun medians(
        reports: List<CorpusReport>,
        engine: String,
        metric: Metric,
    ): Map<String, Long> {
        return reports.flatMap { report -> report.fixtures }
            .filter { measurement -> measurement.engine == engine && measurement.outcome == Outcome.Passed }
            .mapNotNull { measurement -> metricNanos(measurement, metric)?.let { nanos -> measurement.fixture to nanos } }
            .groupBy({ (fixture, _) -> fixture }, { (_, nanos) -> nanos })
            .mapValues { (_, values) -> median(values) }
    }

    private fun metricNanos(
        measurement: Measurement,
        metric: Metric,
    ): Long? {
        return when (metric) {
            Metric.Instantiation -> measurement.cold.moduleNanos + measurement.cold.instantiateNanos
            Metric.ColdExecute -> measurement.cold.executeNanos
            Metric.WarmExecute -> measurement.warm?.executeNanos
        }
    }

    private fun median(values: List<Long>): Long {

        val sorted = values.sorted()
        val middle = sorted.size / 2

        return if (sorted.size % 2 == 0) {
            (sorted[middle - 1] + sorted[middle]) / 2
        } else {
            sorted[middle]
        }
    }

    private fun compareMetric(
        metric: Metric,
        before: Map<String, Long>,
        after: Map<String, Long>,
    ): MetricComparison? {

        val changes = before.keys.filter { fixture -> fixture in after }
            .sorted()
            .map { fixture -> FixtureChange(fixture, before.getValue(fixture), after.getValue(fixture)) }
            .filter { change -> change.beforeNanos > 0 && change.afterNanos > 0 }
        val noisy = changes.filter { change -> change.fixture in noisyTargets }
        val compared = changes.filter { change -> change.fixture !in noisyTargets && change.beforeNanos >= FLOOR_NANOS }

        if (compared.isEmpty()) {
            return null
        }

        val ranked = compared.sortedBy(FixtureChange::ratio)
        val beforeNanos = compared.sumOf(FixtureChange::beforeNanos)
        val afterNanos = compared.sumOf(FixtureChange::afterNanos)

        return MetricComparison(
            metric = metric,
            fixtureCount = compared.size,
            geomean = exp(compared.sumOf { change -> ln(change.ratio) } / compared.size),
            timeWeighted = afterNanos.toDouble() / beforeNanos,
            fasterCount = compared.count { change -> change.ratio < 1 - CHANGE_THRESHOLD },
            slowerCount = compared.count { change -> change.ratio > 1 + CHANGE_THRESHOLD },
            beforeNanos = beforeNanos,
            afterNanos = afterNanos,
            best = ranked.take(FIXTURES_SHOWN),
            worst = ranked.takeLast(FIXTURES_SHOWN).reversed(),
            noisy = noisy,
        )
    }

    private companion object {
        const val FLOOR_NANOS = 1_000_000L
        const val CHANGE_THRESHOLD = 0.03
        const val FIXTURES_SHOWN = 6
        const val ALL_TARGETS = "all"
        val COMPARED_PROPERTIES = listOf<Pair<String, (CorpusReport) -> String>>(
            "environment" to { report -> report.environment.name },
            "model" to { report -> report.environment.machine.model.toString() },
            "phase" to { report -> report.configuration.phase },
            "engines" to { report -> report.summary.map(EngineSummary::engine).sorted().joinToString() },
            "targets" to { report -> report.configuration.targets.sorted().joinToString().ifEmpty { ALL_TARGETS } },
            "warm-ups" to { report -> report.configuration.warmups.toString() },
            "rounds" to { report -> report.configuration.rounds.toString() },
            "pin" to { report -> report.configuration.pin.toString() },
            "corpus revision" to { report -> report.configuration.revision },
        )
    }
}
