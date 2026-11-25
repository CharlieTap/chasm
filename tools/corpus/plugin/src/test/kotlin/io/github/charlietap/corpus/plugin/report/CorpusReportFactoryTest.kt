package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CorpusReportFactoryTest {

    @Test
    fun `an expected engine with no measurements lists every fixture as missing`() {

        val measurements = listOf(
            measurement("activity_selection", "interpreter", Outcome.Passed, timings(10), timings(1)),
            measurement("sqlite3", "interpreter", Outcome.Skipped, timings(0), null),
        )

        val report = CorpusReportFactory().create(
            environment = environment(),
            environmentEngines = setOf("interpreter", "compiled"),
            configuration = configuration("invocation", listOf("interpreter", "compiled")),
            fixtures = FIXTURES,
            measurements = measurements,
        )

        assertEquals(listOf("interpreter", "compiled"), report.summary.map { summary -> summary.engine })
        assertEquals(1, report.summary[0].passed)
        assertEquals(1, report.summary[0].skipped)
        assertEquals(emptyList(), report.summary[0].missing)
        assertEquals(timings(10), report.summary[0].cold)
        assertEquals(timings(1), report.summary[0].warm)
        assertEquals(0, report.summary[1].passed)
        assertEquals(FIXTURES, report.summary[1].missing)
        assertEquals(2, report.schemaVersion)
    }

    @Test
    fun `only configured engines the environment has are expected`() {

        val report = CorpusReportFactory().create(
            environment = environment(),
            environmentEngines = setOf("interpreter"),
            configuration = configuration("invocation", listOf("interpreter", "compiled")),
            fixtures = FIXTURES,
            measurements = emptyList(),
        )

        assertEquals(listOf("interpreter"), report.summary.map { summary -> summary.engine })
    }

    @Test
    fun `front end phases expect the frontend engine only`() {

        val report = CorpusReportFactory().create(
            environment = environment(),
            environmentEngines = setOf("interpreter", "compiled"),
            configuration = configuration("decoding", listOf("interpreter", "compiled")),
            fixtures = FIXTURES,
            measurements = emptyList(),
        )

        assertEquals(listOf("frontend"), report.summary.map { summary -> summary.engine })
    }

    @Test
    fun `warm totals are left out unless every passed fixture has them`() {

        val measurements = listOf(
            measurement("activity_selection", "compiled", Outcome.Passed, timings(10), timings(1)),
            measurement("sqlite3", "compiled", Outcome.Passed, timings(20), null),
        )

        val report = CorpusReportFactory().create(
            environment = environment(),
            environmentEngines = setOf("compiled"),
            configuration = configuration("invocation", listOf("compiled")),
            fixtures = FIXTURES,
            measurements = measurements,
        )

        assertEquals(timings(30), report.summary.single().cold)
        assertNull(report.summary.single().warm)
    }

    @Test
    fun `fixtures are sorted by engine, version and name`() {

        val measurements = listOf(
            measurement("sqlite3", "interpreter", Outcome.Passed, timings(1), null),
            measurement("sqlite3", "compiled", Outcome.Passed, timings(1), null),
            measurement("activity_selection", "interpreter", Outcome.Passed, timings(1), null),
        )

        val report = CorpusReportFactory().create(
            environment = environment(),
            environmentEngines = setOf("interpreter", "compiled"),
            configuration = configuration("invocation", listOf("interpreter", "compiled")),
            fixtures = FIXTURES,
            measurements = measurements,
        )

        assertEquals(
            listOf("compiled/sqlite3", "interpreter/activity_selection", "interpreter/sqlite3"),
            report.fixtures.map { measurement -> "${measurement.engine}/${measurement.fixture}" },
        )
    }

    private fun measurement(
        fixture: String,
        engine: String,
        outcome: Outcome,
        cold: Timings,
        warm: Timings?,
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

    private fun timings(nanos: Long): Timings {
        return Timings(
            totalNanos = nanos,
            decodeNanos = 0,
            validateNanos = 0,
            moduleNanos = nanos,
            instantiateNanos = nanos,
            executeNanos = nanos,
        )
    }

    private fun environment(): CorpusEnvironment {
        return CorpusEnvironment(
            name = "jvm",
            machine = CorpusMachine("Mac OS X", "27.0.1", "aarch64", "Mac17,9", "Apple M5 Pro", 18, 25_769_803_776),
            runtime = CorpusRuntime("Eclipse Temurin", "25.0.3+9-LTS", null),
            conditions = emptyMap(),
        )
    }

    private fun configuration(
        phase: String,
        engines: List<String>,
    ): CorpusConfiguration {
        return CorpusConfiguration(
            repositoryUrl = "https://github.com/CharlieTap/wasm-corpus.git",
            revision = "13f6c9049f35df5430516691f9ee0a69ad5d573a",
            phase = phase,
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
        )
    }

    private companion object {
        val FIXTURES = listOf("activity_selection", "sqlite3")
    }
}
