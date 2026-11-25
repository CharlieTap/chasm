package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import io.github.charlietap.corpus.lib.measurement.Outcome
import io.github.charlietap.corpus.lib.measurement.Timings
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstrumentationOutputTest {

    @Test
    fun `a passing run writes every measurement as it arrives and completes`() {

        val writer = RecordingMeasurementWriter()
        val output = InstrumentationOutput(writer)

        passingRun().lines().forEach { line -> output.accept(line) }

        assertEquals(listOf(measurement(ACTIVITY_SELECTION), measurement(SQLITE)), writer.measurements)
        assertEquals(2, output.measurementCount)
        assertTrue(output.completed)
        assertEquals(emptyList(), output.failures)
        assertEquals(mapOf("threadPriority" to "-8", "allowedCpus" to "0-9"), output.threadConditions())
    }

    @Test
    fun `a failing test is kept with its class, name and whole stack`() {

        val writer = RecordingMeasurementWriter()
        val output = InstrumentationOutput(writer)

        failingRun().lines().forEach { line -> output.accept(line) }

        val stack = listOf(
            "java.lang.AssertionError: Failure(fixture=sqlite3, context=test 1, message=trap)",
            "\tat org.junit.Assert.fail(Assert.java:89)",
            "\tat io.github.charlietap.chasm.corpus.generated.CorpusTest.run(CorpusTest.kt:24)",
        ).joinToString("\n")
        assertEquals(listOf(InstrumentationFailure("$TEST_CLASS#sqlite3", stack)), output.failures)
        assertEquals(listOf(measurement(SQLITE)), writer.measurements)
        assertTrue(output.completed)
        assertEquals("$TEST_CLASS#sqlite3 failed\n$stack", output.describeFailure())
    }

    @Test
    fun `a crash keeps the measurements before it and reports an incomplete run with the test it was running`() {

        val writer = RecordingMeasurementWriter()
        val output = InstrumentationOutput(writer)

        crashedRun().lines().forEach { line -> output.accept(line) }

        assertEquals(listOf(measurement(ACTIVITY_SELECTION)), writer.measurements)
        assertFalse(output.completed)
        assertEquals(
            "The instrumentation run did not complete after 1 measurements while running $TEST_CLASS#sqlite3: " +
                "shortMsg=Process crashed.",
            output.describeFailure(),
        )
    }

    @Test
    fun `output cut off before the result reports an incomplete run`() {

        val output = InstrumentationOutput(RecordingMeasurementWriter())

        started(ACTIVITY_SELECTION).lines().forEach { line -> output.accept(line) }

        assertFalse(output.completed)
        assertEquals(
            "The instrumentation run did not complete after 0 measurements while running $TEST_CLASS#activity_selection: " +
                "the output ended without a result",
            output.describeFailure(),
        )
    }

    @Test
    fun `lines ending in a carriage return are read like plain lines`() {

        val writer = RecordingMeasurementWriter()
        val output = InstrumentationOutput(writer)

        passingRun().lines().forEach { line -> output.accept("$line\r") }

        assertEquals(listOf(measurement(ACTIVITY_SELECTION), measurement(SQLITE)), writer.measurements)
        assertTrue(output.completed)
    }

    @Test
    fun `distinct thread conditions are all kept`() {

        val output = InstrumentationOutput(RecordingMeasurementWriter())

        listOf(
            thread("-8", "0-9"),
            thread("-8", "4-9"),
            thread("-4", "0-9"),
        ).joinToString("\n").lines().forEach { line -> output.accept(line) }

        assertEquals(mapOf("threadPriority" to "-8; -4", "allowedCpus" to "0-9; 4-9"), output.threadConditions())
    }

    @Test
    fun `a pinned run records the pinned cpu beside the allowed cpus read back`() {

        val output = InstrumentationOutput(RecordingMeasurementWriter())

        listOf(
            thread("-8", "7", pinnedCpu = "7"),
            thread("-8", "7", pinnedCpu = "7"),
        ).joinToString("\n").lines().forEach { line -> output.accept(line) }

        assertEquals(
            mapOf("threadPriority" to "-8", "allowedCpus" to "7", "pinnedCpu" to "7"),
            output.threadConditions(),
        )
    }

    @Test
    fun `a run without thread conditions records none`() {

        val output = InstrumentationOutput(RecordingMeasurementWriter())

        listOf(started(ACTIVITY_SELECTION), finished(ACTIVITY_SELECTION), completed()).joinToString("\n")
            .lines()
            .forEach { line -> output.accept(line) }

        assertEquals(emptyMap(), output.threadConditions())
    }

    private fun passingRun(): String {
        return listOf(
            started(ACTIVITY_SELECTION),
            thread("-8", "0-9"),
            measured(ACTIVITY_SELECTION),
            finished(ACTIVITY_SELECTION),
            started(SQLITE),
            thread("-8", "0-9"),
            measured(SQLITE),
            finished(SQLITE),
            completed(),
        ).joinToString("\n")
    }

    private fun failingRun(): String {
        return listOf(
            started(SQLITE),
            measured(SQLITE),
            """
            |INSTRUMENTATION_STATUS: class=$TEST_CLASS
            |INSTRUMENTATION_STATUS: current=1
            |INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
            |INSTRUMENTATION_STATUS: numtests=2
            |INSTRUMENTATION_STATUS: stack=java.lang.AssertionError: Failure(fixture=sqlite3, context=test 1, message=trap)
            |${"\t"}at org.junit.Assert.fail(Assert.java:89)
            |${"\t"}at io.github.charlietap.chasm.corpus.generated.CorpusTest.run(CorpusTest.kt:24)
            |
            |INSTRUMENTATION_STATUS: stream=
            |Error in sqlite3($TEST_CLASS):
            |java.lang.AssertionError: Failure(fixture=sqlite3, context=test 1, message=trap)
            |INSTRUMENTATION_STATUS: test=sqlite3
            |INSTRUMENTATION_STATUS_CODE: -2
            """.trimMargin(),
            """
            |INSTRUMENTATION_RESULT: stream=
            |
            |Time: 0.512
            |There was 1 failure:
            |
            |FAILURES!!!
            |Tests run: 1,  Failures: 1
            |
            |
            |INSTRUMENTATION_CODE: -1
            """.trimMargin(),
        ).joinToString("\n")
    }

    private fun crashedRun(): String {
        return listOf(
            started(ACTIVITY_SELECTION),
            measured(ACTIVITY_SELECTION),
            finished(ACTIVITY_SELECTION),
            started(SQLITE),
            """
            |INSTRUMENTATION_RESULT: shortMsg=Process crashed.
            |INSTRUMENTATION_CODE: 0
            """.trimMargin(),
        ).joinToString("\n")
    }

    private fun started(fixture: String): String {
        return """
            |INSTRUMENTATION_STATUS: class=$TEST_CLASS
            |INSTRUMENTATION_STATUS: current=1
            |INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
            |INSTRUMENTATION_STATUS: numtests=2
            |INSTRUMENTATION_STATUS: stream=
            |$TEST_CLASS:
            |INSTRUMENTATION_STATUS: test=$fixture
            |INSTRUMENTATION_STATUS_CODE: 1
            """.trimMargin()
    }

    private fun finished(fixture: String): String {
        return """
            |INSTRUMENTATION_STATUS: class=$TEST_CLASS
            |INSTRUMENTATION_STATUS: current=1
            |INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
            |INSTRUMENTATION_STATUS: numtests=2
            |INSTRUMENTATION_STATUS: stream=.
            |INSTRUMENTATION_STATUS: test=$fixture
            |INSTRUMENTATION_STATUS_CODE: 0
            """.trimMargin()
    }

    private fun measured(fixture: String): String {
        return """
            |INSTRUMENTATION_STATUS: corpus.measurement=${Json.encodeToString(Measurement.serializer(), measurement(fixture))}
            |INSTRUMENTATION_STATUS_CODE: 2
            """.trimMargin()
    }

    private fun thread(
        priority: String,
        cpus: String,
        pinnedCpu: String? = null,
    ): String {

        val pinned = pinnedCpu?.let { cpu -> " pinnedCpu=$cpu" }.orEmpty()

        return """
            |INSTRUMENTATION_STATUS: corpus.thread=priority=$priority cpus=$cpus$pinned
            |INSTRUMENTATION_STATUS_CODE: 2
            """.trimMargin()
    }

    private fun completed(): String {
        return """
            |INSTRUMENTATION_RESULT: stream=
            |
            |Time: 1.024
            |
            |OK (2 tests)
            |
            |
            |INSTRUMENTATION_CODE: -1
            """.trimMargin()
    }

    private fun measurement(fixture: String): Measurement {
        return Measurement(
            fixture = fixture,
            version = "1.0",
            path = "$fixture.wasm",
            sha256 = null,
            engine = "compiled",
            outcome = Outcome.Passed,
            detail = "a=b",
            binaryBytes = 721,
            tests = 3,
            steps = 3,
            instances = 3,
            cold = Timings(
                totalNanos = 600,
                decodeNanos = 0,
                validateNanos = 0,
                moduleNanos = 300,
                instantiateNanos = 200,
                executeNanos = 100,
            ),
            warm = null,
        )
    }

    private class RecordingMeasurementWriter : MeasurementWriter {

        val measurements = mutableListOf<Measurement>()

        override fun write(measurement: Measurement) {
            measurements += measurement
        }
    }

    private companion object {
        const val TEST_CLASS = "io.github.charlietap.chasm.corpus.generated.CorpusTest"
        const val ACTIVITY_SELECTION = "activity_selection"
        const val SQLITE = "sqlite3"
    }
}
