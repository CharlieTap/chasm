package io.github.charlietap.chasm.corpus

import android.app.Instrumentation
import android.os.Bundle
import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.measurement.MeasurementWriter
import kotlinx.serialization.json.Json

class InstrumentationMeasurementWriter(
    private val instrumentation: Instrumentation,
    private val json: Json,
) : MeasurementWriter {

    override fun write(measurement: Measurement) {

        val status = Bundle()
        status.putString(MEASUREMENT_STATUS_KEY, json.encodeToString(Measurement.serializer(), measurement))

        instrumentation.sendStatus(IN_PROGRESS, status)
    }

    private companion object {
        const val MEASUREMENT_STATUS_KEY = "corpus.measurement"
        const val IN_PROGRESS = 2
    }
}
