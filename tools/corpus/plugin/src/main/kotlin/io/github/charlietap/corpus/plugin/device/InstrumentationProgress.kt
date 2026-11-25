package io.github.charlietap.corpus.plugin.device

import org.gradle.api.logging.Logger

class InstrumentationProgress(
    private val logger: Logger,
) {

    private var reportedMeasurements = 0
    private var reportedFailures = 0

    fun report(output: InstrumentationOutput) {

        if (output.measurementCount >= reportedMeasurements + MEASUREMENTS_PER_REPORT) {
            reportedMeasurements = output.measurementCount
            logger.lifecycle("Received ${output.measurementCount} corpus measurements from the device")
        }

        output.failures.drop(reportedFailures).forEach { failure ->
            logger.lifecycle("${failure.test} failed on the device")
        }
        reportedFailures = output.failures.size
    }

    private companion object {
        const val MEASUREMENTS_PER_REPORT = 50
    }
}
