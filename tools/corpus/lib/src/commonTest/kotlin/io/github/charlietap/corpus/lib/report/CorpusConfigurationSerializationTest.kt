package io.github.charlietap.corpus.lib.report

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CorpusConfigurationSerializationTest {

    @Test
    fun `an unpinned configuration still records pin`() {

        val encoded = Json.encodeToString(CorpusConfiguration.serializer(), configuration(pin = false))

        assertFalse(Json.parseToJsonElement(encoded).jsonObject.getValue("pin").jsonPrimitive.boolean)
    }

    @Test
    fun `a configuration recorded before pin existed decodes as unpinned`() {

        val encoded = Json.encodeToString(CorpusConfiguration.serializer(), configuration(pin = false))
        val withoutPin = JsonObject(Json.parseToJsonElement(encoded).jsonObject.minus("pin"))

        val decoded = Json.decodeFromString(CorpusConfiguration.serializer(), withoutPin.toString())

        assertEquals(configuration(pin = false), decoded)
    }

    @Test
    fun `a pinned configuration decodes back to the same configuration`() {

        val configuration = configuration(pin = true)

        val encoded = Json.encodeToString(CorpusConfiguration.serializer(), configuration)

        assertEquals(configuration, Json.decodeFromString(CorpusConfiguration.serializer(), encoded))
    }

    private fun configuration(pin: Boolean): CorpusConfiguration {
        return CorpusConfiguration(
            repositoryUrl = "https://github.com/CharlieTap/wasm-corpus.git",
            revision = "13f6c9049f35df5430516691f9ee0a69ad5d573a",
            phase = "invocation",
            engines = listOf("compiled"),
            warmups = 0,
            rounds = 0,
            pin = pin,
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
}
