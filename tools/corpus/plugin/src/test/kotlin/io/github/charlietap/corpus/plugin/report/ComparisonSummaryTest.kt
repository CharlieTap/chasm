package io.github.charlietap.corpus.plugin.report

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ComparisonSummaryTest {

    @Test
    fun `a comparison prints warnings, ratios, totals, rankings and noisy fixtures`() {

        val change = FixtureChange("sqlite3", 10_000_000, 10_000_000)
        val noisy = FixtureChange("lzma_wasm", 10_000_000, 12_000_000)
        val comparison = Comparison(
            differences = listOf(Difference("rounds", listOf("3", "0"))),
            engines = listOf(
                EngineComparison(
                    engine = "interpreter",
                    metrics = listOf(
                        MetricComparison(
                            metric = Metric.ColdExecute,
                            fixtureCount = 1,
                            geomean = 1.0,
                            timeWeighted = 1.0,
                            fasterCount = 0,
                            slowerCount = 0,
                            beforeNanos = 10_000_000,
                            afterNanos = 10_000_000,
                            best = listOf(change),
                            worst = listOf(change),
                            noisy = listOf(noisy),
                        ),
                    ),
                ),
            ),
        )

        val summary = ComparisonSummary().describe(comparison, listOf(File("/before.json")), listOf(File("/after.json")))

        assertEquals(
            """
            Corpus comparison, after / before
              before: /before.json
              after: /after.json
              warning: rounds differs: 3, 0
              interpreter
                cold execute: 1 fixtures >= 1 ms, geomean 1.000, time-weighted 1.000, 0 faster and 0 slower beyond 3 %, total 10.0 -> 10.0 ms
                  best: sqlite3 1.000
                  worst: sqlite3 1.000
                  noisy, not in the ratios: lzma_wasm 1.200
            """.trimIndent(),
            summary,
        )
    }
}
