package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import io.github.charlietap.corpus.lib.report.EngineSummary
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportComparisonTest {

    @Test
    fun `a report compared with itself has ratios of one`() {

        val report = report(
            measurement("activity_selection", cold(2 * MS, 4 * MS), warm(1 * MS)),
            measurement("sqlite3", cold(30 * MS, 50 * MS), warm(20 * MS)),
        )

        val comparison = ReportComparison(emptySet()).compare(listOf(report), listOf(report))
        val metrics = comparison.engines.single().metrics

        assertEquals(emptyList(), comparison.differences)
        assertEquals(listOf(Metric.Instantiation, Metric.ColdExecute, Metric.WarmExecute), metrics.map { metric -> metric.metric })
        metrics.forEach { metric ->
            assertEquals(1.0, metric.geomean)
            assertEquals(1.0, metric.timeWeighted)
            assertEquals(0, metric.fasterCount)
            assertEquals(0, metric.slowerCount)
            assertEquals(metric.beforeNanos, metric.afterNanos)
        }
    }

    @Test
    fun `ratios are after over before with geomean, time weighted and moves beyond three percent`() {

        val before = report(
            measurement("a", cold(MS, 10 * MS), null),
            measurement("b", cold(MS, 40 * MS), null),
            measurement("c", cold(MS, 100 * MS), null),
        )
        val after = report(
            measurement("a", cold(MS, 5 * MS), null),
            measurement("b", cold(MS, 80 * MS), null),
            measurement("c", cold(MS, 102 * MS), null),
        )

        val execute = coldExecute(ReportComparison(emptySet()).compare(listOf(before), listOf(after)))

        assertEquals(3, execute.fixtureCount)
        assertEquals(Math.cbrt(0.5 * 2.0 * 1.02), execute.geomean, 1e-9)
        assertEquals(187.0 / 150.0, execute.timeWeighted, 1e-9)
        assertEquals(1, execute.fasterCount)
        assertEquals(1, execute.slowerCount)
        assertEquals(150 * MS, execute.beforeNanos)
        assertEquals(187 * MS, execute.afterNanos)
    }

    @Test
    fun `fixtures under the one millisecond floor before the change are left out`() {

        val before = report(
            measurement("small", cold(MS, MS - 1), null),
            measurement("large", cold(MS, MS), null),
        )
        val after = report(
            measurement("small", cold(MS, 10 * MS), null),
            measurement("large", cold(MS, MS), null),
        )

        val execute = coldExecute(ReportComparison(emptySet()).compare(listOf(before), listOf(after)))

        assertEquals(1, execute.fixtureCount)
        assertEquals(listOf("large"), execute.best.map { change -> change.fixture })
    }

    @Test
    fun `noisy targets are listed separately and left out of the ratios`() {

        val before = report(
            measurement("lzma_wasm", cold(MS, 10 * MS), null),
            measurement("sqlite3", cold(MS, 10 * MS), null),
        )
        val after = report(
            measurement("lzma_wasm", cold(MS, 20 * MS), null),
            measurement("sqlite3", cold(MS, 10 * MS), null),
        )

        val execute = coldExecute(ReportComparison(setOf("lzma_wasm")).compare(listOf(before), listOf(after)))

        assertEquals(1, execute.fixtureCount)
        assertEquals(1.0, execute.timeWeighted)
        assertEquals(listOf(FixtureChange("lzma_wasm", 10 * MS, 20 * MS)), execute.noisy)
    }

    @Test
    fun `each side takes the median per fixture across its reports`() {

        val before = listOf(10L, 30L, 20L).map { value -> report(measurement("sqlite3", cold(MS, value * MS), null)) }
        val after = listOf(40L, 10L).map { value -> report(measurement("sqlite3", cold(MS, value * MS), null)) }

        val execute = coldExecute(ReportComparison(emptySet()).compare(before, after))

        assertEquals(listOf(FixtureChange("sqlite3", 20 * MS, 25 * MS)), execute.best)
    }

    @Test
    fun `instantiation is module preparation plus instantiation of the cold pass`() {

        val before = report(measurement("sqlite3", Timings(0, 0, 0, MS, 2 * MS, MS), null))
        val after = report(measurement("sqlite3", Timings(0, 0, 0, 2 * MS, 4 * MS, MS), null))

        val instantiation = ReportComparison(emptySet()).compare(listOf(before), listOf(after))
            .engines.single().metrics.first { metric -> metric.metric == Metric.Instantiation }

        assertEquals(FixtureChange("sqlite3", 3 * MS, 6 * MS), instantiation.best.single())
    }

    @Test
    fun `warm execute is compared only when both sides have warm timings`() {

        val before = report(measurement("sqlite3", cold(MS, MS), warm(MS)))
        val after = report(measurement("sqlite3", cold(MS, MS), null))

        val metrics = ReportComparison(emptySet()).compare(listOf(before), listOf(after)).engines.single().metrics

        assertEquals(listOf(Metric.Instantiation, Metric.ColdExecute), metrics.map { metric -> metric.metric })
    }

    @Test
    fun `only passed fixtures on both sides are compared`() {

        val before = report(
            measurement("a", cold(MS, MS), null),
            measurement("b", cold(MS, MS), null),
            measurement("c", cold(MS, MS), null),
        )
        val after = report(
            measurement("a", cold(MS, MS), null),
            measurement("b", cold(MS, MS), null, Outcome.Failed),
        )

        val execute = coldExecute(ReportComparison(emptySet()).compare(listOf(before), listOf(after)))

        assertEquals(listOf("a"), execute.best.map { change -> change.fixture })
    }

    @Test
    fun `the six best and six worst fixtures are ranked by ratio`() {

        val names = (1..14).map { index -> "f$index" }
        val before = report(*names.map { name -> measurement(name, cold(MS, 100 * MS), null) }.toTypedArray())
        val after = report(
            *names.mapIndexed { index, name -> measurement(name, cold(MS, (90L + index) * MS), null) }.toTypedArray(),
        )

        val execute = coldExecute(ReportComparison(emptySet()).compare(listOf(before), listOf(after)))

        assertEquals(listOf("f1", "f2", "f3", "f4", "f5", "f6"), execute.best.map { change -> change.fixture })
        assertEquals(listOf("f14", "f13", "f12", "f11", "f10", "f9"), execute.worst.map { change -> change.fixture })
    }

    @Test
    fun `differences in environment, model, phase, counts, pinning and revision are reported`() {

        val before = report(measurement("sqlite3", cold(MS, MS), null))
        val after = before.copy(
            environment = before.environment.copy(
                name = "androidDevice",
                machine = before.environment.machine.copy(model = "SM-S721B"),
            ),
            configuration = before.configuration.copy(
                phase = "instantiation",
                warmups = 1,
                rounds = 3,
                pin = true,
                revision = "main",
            ),
        )

        val differences = ReportComparison(emptySet()).compare(listOf(before), listOf(after)).differences

        assertEquals(
            listOf(
                Difference("environment", listOf("jvm", "androidDevice")),
                Difference("model", listOf("Mac17,9", "SM-S721B")),
                Difference("phase", listOf("invocation", "instantiation")),
                Difference("warm-ups", listOf("0", "1")),
                Difference("rounds", listOf("0", "3")),
                Difference("pin", listOf("false", "true")),
                Difference("corpus revision", listOf(REVISION, "main")),
            ),
            differences,
        )
    }

    @Test
    fun `differences in engines and targets are reported`() {

        val before = report(measurement("sqlite3", cold(MS, MS), null))
        val bothEngines = report(
            measurement("sqlite3", cold(MS, MS), null),
            measurement("sqlite3", cold(MS, MS), null, engine = "compiled"),
        )
        val after = bothEngines.copy(configuration = bothEngines.configuration.copy(targets = listOf("zlib", "sqlite3")))

        val differences = ReportComparison(emptySet()).compare(listOf(before), listOf(after)).differences

        assertEquals(
            listOf(
                Difference("engines", listOf("interpreter", "compiled, interpreter")),
                Difference("targets", listOf("all", "sqlite3, zlib")),
            ),
            differences,
        )
    }

    @Test
    fun `only engines on both sides are compared`() {

        val before = report(measurement("sqlite3", cold(MS, MS), null))
        val after = report(measurement("sqlite3", cold(MS, MS), null, engine = "compiled"))

        val comparison = ReportComparison(emptySet()).compare(listOf(before), listOf(after))

        assertEquals(emptyList(), comparison.engines)
    }

    private fun coldExecute(comparison: Comparison): MetricComparison {
        return comparison.engines.single().metrics.first { metric -> metric.metric == Metric.ColdExecute }
    }

    private fun report(vararg measurements: Measurement): CorpusReport {

        val engines = measurements.map { measurement -> measurement.engine }.distinct()
        val zero = Timings(0, 0, 0, 0, 0, 0)

        return CorpusReport(
            schemaVersion = 2,
            environment = CorpusEnvironment(
                name = "jvm",
                machine = CorpusMachine("Mac OS X", "27.0.1", "aarch64", "Mac17,9", "Apple M5 Pro", 18, 25_769_803_776),
                runtime = CorpusRuntime("Eclipse Temurin", "25.0.3+9-LTS", null),
                conditions = emptyMap(),
            ),
            configuration = CorpusConfiguration(
                repositoryUrl = "https://github.com/CharlieTap/wasm-corpus.git",
                revision = REVISION,
                phase = "invocation",
                engines = engines,
                warmups = 0,
                rounds = 0,
                versions = listOf("1.0"),
                languages = emptyList(),
                requiredFeatures = emptyList(),
                excludedFeatures = emptyList(),
                tags = emptyList(),
                excludedTags = emptyList(),
                size = null,
                duration = null,
                targets = emptyList(),
                excludedTargets = emptyList(),
            ),
            summary = engines.map { engine -> EngineSummary(engine, 0, 0, 0, emptyList(), zero, null) },
            fixtures = measurements.toList(),
        )
    }

    private fun measurement(
        fixture: String,
        cold: Timings,
        warm: Timings?,
        outcome: Outcome = Outcome.Passed,
        engine: String = "interpreter",
    ): Measurement {
        return Measurement(
            fixture = fixture,
            version = "1.0",
            path = "$fixture.wasm",
            sha256 = null,
            engine = engine,
            outcome = outcome,
            detail = null,
            binaryBytes = 721,
            tests = 1,
            steps = 1,
            instances = 1,
            cold = cold,
            warm = warm,
        )
    }

    private fun cold(
        instantiation: Long,
        execute: Long,
    ): Timings {
        return Timings(
            totalNanos = instantiation + execute,
            decodeNanos = 0,
            validateNanos = 0,
            moduleNanos = 0,
            instantiateNanos = instantiation,
            executeNanos = execute,
        )
    }

    private fun warm(execute: Long): Timings {
        return Timings(
            totalNanos = execute,
            decodeNanos = 0,
            validateNanos = 0,
            moduleNanos = 0,
            instantiateNanos = 0,
            executeNanos = execute,
        )
    }

    private companion object {
        const val MS = 1_000_000L
        const val REVISION = "13f6c9049f35df5430516691f9ee0a69ad5d573a"
    }
}
