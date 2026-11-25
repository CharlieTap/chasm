package io.github.charlietap.corpus.lib.measurement

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MeasurementSerializationTest {

    @Test
    fun `a measurement encodes to one line even when its detail spans several`() {

        val encoded = Json.encodeToString(Measurement.serializer(), measurement())

        assertFalse('\n' in encoded, encoded)
        assertFalse('\r' in encoded, encoded)
    }

    @Test
    fun `an encoded measurement decodes back to the same measurement`() {

        val measurement = measurement()

        val encoded = Json.encodeToString(Measurement.serializer(), measurement)

        assertEquals(measurement, Json.decodeFromString(Measurement.serializer(), encoded))
    }

    private fun measurement(): Measurement {
        return Measurement(
            fixture = "sqlite3",
            version = "2.0",
            path = "sqlite3.wasm",
            sha256 = null,
            engine = "interpreter",
            outcome = Outcome.Failed,
            detail = "test 2 step 1\r\nexpected: i32 1\nactual: i32 0",
            binaryBytes = 1_048_576,
            tests = 4,
            steps = 12,
            instances = 4,
            cold = Timings(
                totalNanos = 1_000,
                decodeNanos = 100,
                validateNanos = 200,
                moduleNanos = 300,
                instantiateNanos = 150,
                executeNanos = 250,
            ),
            warm = null,
        )
    }
}
