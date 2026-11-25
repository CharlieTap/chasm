package io.github.charlietap.corpus.plugin.report

data class Comparison(
    val differences: List<Difference>,
    val engines: List<EngineComparison>,
)

data class Difference(
    val property: String,
    val values: List<String>,
)

data class EngineComparison(
    val engine: String,
    val metrics: List<MetricComparison>,
)

data class MetricComparison(
    val metric: Metric,
    val fixtureCount: Int,
    val geomean: Double,
    val timeWeighted: Double,
    val fasterCount: Int,
    val slowerCount: Int,
    val beforeNanos: Long,
    val afterNanos: Long,
    val best: List<FixtureChange>,
    val worst: List<FixtureChange>,
    val noisy: List<FixtureChange>,
)

data class FixtureChange(
    val fixture: String,
    val beforeNanos: Long,
    val afterNanos: Long,
) {
    val ratio: Double
        get() {
            return afterNanos.toDouble() / beforeNanos
        }
}

enum class Metric(val label: String) {
    Instantiation("instantiation"),
    ColdExecute("cold execute"),
    WarmExecute("warm execute"),
}
