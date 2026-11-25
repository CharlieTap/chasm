package io.github.charlietap.corpus.plugin.device

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import kotlinx.serialization.json.Json

data class InstrumentationFailure(
    val test: String,
    val stack: String,
)

class InstrumentationOutput(
    private val measurements: MeasurementWriter,
) {

    private val statusEntries = mutableMapOf<String, StringBuilder>()
    private val resultEntries = mutableMapOf<String, StringBuilder>()
    private var openValue: StringBuilder? = null
    private var runningTest: String? = null

    private val priorities = linkedSetOf<String>()
    private val cpus = linkedSetOf<String>()
    private val pinnedCpus = linkedSetOf<String>()
    private val recordedFailures = mutableListOf<InstrumentationFailure>()

    var completed = false
        private set

    var measurementCount = 0
        private set

    val failures: List<InstrumentationFailure>
        get() {
            return recordedFailures
        }

    fun accept(line: String) {

        val text = line.removeSuffix("\r")

        when {
            text.startsWith(STATUS) -> {
                openValue = start(statusEntries, text.removePrefix(STATUS))
            }
            text.startsWith(STATUS_CODE) -> {
                close(text.removePrefix(STATUS_CODE).trim().toInt())
            }
            text.startsWith(RESULT) -> {
                openValue = start(resultEntries, text.removePrefix(RESULT))
            }
            text.startsWith(CODE) -> {
                completed = text.removePrefix(CODE).trim() == COMPLETED_CODE
                openValue = null
            }
            text.startsWith(PROTOCOL_PREFIX) -> {
                openValue = null
            }
            else -> {
                openValue?.append('\n')?.append(text)
            }
        }
    }

    fun threadConditions(): Map<String, String> {
        return if (priorities.isEmpty()) {
            emptyMap()
        } else {
            buildMap {
                put(THREAD_PRIORITY, priorities.joinToString(DISTINCT_SEPARATOR))
                put(ALLOWED_CPUS, cpus.joinToString(DISTINCT_SEPARATOR))
                if (pinnedCpus.isNotEmpty()) {
                    put(PINNED_CPU, pinnedCpus.joinToString(DISTINCT_SEPARATOR))
                }
            }
        }
    }

    fun describeFailure(): String {

        val running = runningTest?.let { test -> " while running $test" }.orEmpty()
        val incomplete = if (completed) {
            emptyList()
        } else {
            listOf("The instrumentation run did not complete after $measurementCount measurements$running: ${ending()}")
        }
        val tests = recordedFailures.map { failure -> "${failure.test} failed\n${failure.stack}" }

        return (incomplete + tests).joinToString("\n\n")
    }

    private fun start(
        entries: MutableMap<String, StringBuilder>,
        entry: String,
    ): StringBuilder {

        val builder = StringBuilder(entry.substringAfter('='))
        entries[entry.substringBefore('=')] = builder

        return builder
    }

    private fun close(code: Int) {

        val entries = statusEntries.mapValues { (_, builder) -> builder.toString() }
        statusEntries.clear()
        openValue = null

        val measurement = entries[MEASUREMENT]
        if (measurement != null) {
            measurements.write(Json.decodeFromString(Measurement.serializer(), measurement))
            measurementCount++
        }

        val thread = entries[THREAD]
        if (thread != null) {
            record(thread)
        }

        val test = "${entries[CLASS]}#${entries[TEST]}"
        if (code == START_CODE) {
            runningTest = test
        }
        if (code in TERMINAL_CODES) {
            runningTest = null
        }
        if (code in FAILURE_CODES) {
            recordedFailures += InstrumentationFailure(test, entries[STACK].orEmpty().trimEnd())
        }
    }

    private fun record(thread: String) {

        val fields = thread.split(' ').associate { field -> field.substringBefore('=') to field.substringAfter('=') }

        priorities += fields.getValue(PRIORITY)
        cpus += fields.getValue(CPUS)

        val pinnedCpu = fields[PINNED_CPU]
        if (pinnedCpu != null) {
            pinnedCpus += pinnedCpu
        }
    }

    private fun ending(): String {
        return if (resultEntries.isEmpty()) {
            "the output ended without a result"
        } else {
            resultEntries.entries.joinToString { (key, builder) -> "$key=${builder.toString().trim()}" }
        }
    }

    private companion object {
        const val PROTOCOL_PREFIX = "INSTRUMENTATION_"
        const val STATUS = "INSTRUMENTATION_STATUS: "
        const val STATUS_CODE = "INSTRUMENTATION_STATUS_CODE: "
        const val RESULT = "INSTRUMENTATION_RESULT: "
        const val CODE = "INSTRUMENTATION_CODE: "
        const val COMPLETED_CODE = "-1"
        const val START_CODE = 1
        val TERMINAL_CODES = setOf(0, -1, -2, -3, -4)
        val FAILURE_CODES = setOf(-1, -2)

        const val MEASUREMENT = "corpus.measurement"
        const val THREAD = "corpus.thread"
        const val CLASS = "class"
        const val TEST = "test"
        const val STACK = "stack"

        const val PRIORITY = "priority"
        const val CPUS = "cpus"
        const val PINNED_CPU = "pinnedCpu"
        const val THREAD_PRIORITY = "threadPriority"
        const val ALLOWED_CPUS = "allowedCpus"
        const val DISTINCT_SEPARATOR = "; "
    }
}
