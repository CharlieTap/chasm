package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.measurement.Timings
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import io.github.charlietap.corpus.lib.report.EngineSummary
import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaselineReportCheckTest {

    @Test
    fun `accepts a complete report of every target and engine with the baseline counts`() {

        CHECK.check(report())
    }

    @Test
    fun `refuses a report with failed or missing fixtures`() {

        val failure = assertFailsWith<GradleException> {
            CHECK.check(report(summary = listOf(summary(INTERPRETER), summary(COMPILED).copy(failed = 1))))
        }

        assertEquals("The report is incomplete: [compiled]", failure.message)
    }

    @Test
    fun `refuses other counts and says to request the task by its full name`() {

        val failure = assertFailsWith<GradleException> {
            CHECK.check(report(configuration = configuration().copy(warmups = 0, rounds = 0)))
        }

        assertEquals(
            "A baseline is recorded with 1 warm-ups and 3 rounds, the report has 0 and 0; " +
                "request $TASK by its full name, abbreviated task names are not detected",
            failure.message,
        )
    }

    @Test
    fun `refuses a run narrowed to some targets`() {

        val failure = assertFailsWith<GradleException> {
            CHECK.check(report(configuration = configuration().copy(targets = listOf("sqlite3", "zlib"))))
        }

        assertEquals("A baseline covers every target, the report was narrowed to sqlite3, zlib", failure.message)
    }

    @Test
    fun `refuses a run of another phase`() {

        val failure = assertFailsWith<GradleException> {
            CHECK.check(report(configuration = configuration().copy(phase = "instantiation")))
        }

        assertEquals("A baseline is recorded for the invocation phase, the report measured instantiation", failure.message)
    }

    @Test
    fun `refuses a run narrowed to some engines`() {

        val failure = assertFailsWith<GradleException> {
            CHECK.check(report(summary = listOf(summary(INTERPRETER))))
        }

        assertEquals(
            "A baseline covers every engine of the environment (compiled, interpreter), the report has interpreter",
            failure.message,
        )
    }

    private fun report(
        configuration: CorpusConfiguration = configuration(),
        summary: List<EngineSummary> = listOf(summary(INTERPRETER), summary(COMPILED)),
    ): CorpusReport {
        return CorpusReport(
            schemaVersion = 2,
            environment = CorpusEnvironment(
                name = "jvm",
                machine = CorpusMachine("Mac OS X", "27.0.1", "aarch64", "Mac17,9", "Apple M5 Pro", 18, 25_769_803_776),
                runtime = CorpusRuntime("Eclipse Temurin", "25.0.3+9-LTS", null),
                conditions = emptyMap(),
            ),
            configuration = configuration,
            summary = summary,
            fixtures = emptyList(),
        )
    }

    private fun configuration(): CorpusConfiguration {
        return CorpusConfiguration(
            repositoryUrl = "https://github.com/CharlieTap/wasm-corpus.git",
            revision = "13f6c9049f35df5430516691f9ee0a69ad5d573a",
            phase = "invocation",
            engines = listOf(INTERPRETER, COMPILED),
            warmups = 1,
            rounds = 3,
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

    private fun summary(engine: String): EngineSummary {
        return EngineSummary(
            engine = engine,
            passed = 345,
            skipped = 39,
            failed = 0,
            missing = emptyList(),
            cold = Timings(0, 0, 0, 0, 0, 0),
            warm = null,
        )
    }

    private companion object {
        const val TASK = ":test:corpus:updateCorpusBaseline"
        const val INTERPRETER = "interpreter"
        const val COMPILED = "compiled"
        val CHECK = BaselineReportCheck(
            baselineTask = TASK,
            warmups = 1,
            rounds = 3,
            phase = "invocation",
            environmentEngines = setOf(INTERPRETER, COMPILED),
        )
    }
}
