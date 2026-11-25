package io.github.charlietap.corpus.plugin.report

import java.io.File

class ComparisonSummary {

    fun describe(
        comparison: Comparison,
        before: List<File>,
        after: List<File>,
    ): String {

        val files = listOf(
            "Corpus comparison, after / before",
            "  before: ${before.joinToString { file -> file.absolutePath }}",
            "  after: ${after.joinToString { file -> file.absolutePath }}",
        )
        val warnings = comparison.differences.map { difference ->
            "  warning: ${difference.property} differs: ${difference.values.joinToString()}"
        }
        val engines = comparison.engines.map { engine -> describeEngine(engine) }

        return (files + warnings + engines).joinToString("\n")
    }

    private fun describeEngine(engine: EngineComparison): String {

        val header = "  ${engine.engine}"
        val metrics = engine.metrics.map { metric -> describeMetric(metric) }

        return (listOf(header) + metrics).joinToString("\n")
    }

    private fun describeMetric(metric: MetricComparison): String {

        val ratios = "    ${metric.metric.label}: ${metric.fixtureCount} fixtures >= 1 ms, " +
            "geomean ${ratio(metric.geomean)}, time-weighted ${ratio(metric.timeWeighted)}, " +
            "${metric.fasterCount} faster and ${metric.slowerCount} slower beyond 3 %, " +
            "total ${milliseconds(metric.beforeNanos)} -> ${milliseconds(metric.afterNanos)} ms"
        val best = "      best: ${describeChanges(metric.best)}"
        val worst = "      worst: ${describeChanges(metric.worst)}"
        val noisy = if (metric.noisy.isEmpty()) {
            emptyList()
        } else {
            listOf("      noisy, not in the ratios: ${describeChanges(metric.noisy)}")
        }

        return (listOf(ratios, best, worst) + noisy).joinToString("\n")
    }

    private fun describeChanges(changes: List<FixtureChange>): String {
        return changes.joinToString { change -> "${change.fixture} ${ratio(change.ratio)}" }
    }

    private fun ratio(value: Double): String {
        return "%.3f".format(value)
    }

    private fun milliseconds(nanos: Long): String {
        return "%.1f".format(nanos / NANOS_PER_MILLISECOND)
    }

    private companion object {
        const val NANOS_PER_MILLISECOND = 1_000_000.0
    }
}
