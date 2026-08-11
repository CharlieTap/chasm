package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

object ComponentResultSerializer : KSerializer<ComponentResult> {

    override val descriptor: SerialDescriptor =
        MapSerializer(String.serializer(), JsonElement.serializer()).descriptor

    override fun deserialize(decoder: Decoder): ComponentResult {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("Component results require JSON input")
        val result = jsonDecoder.decodeJsonElement() as? JsonObject
            ?: throw SerializationException("Component results must be objects")

        if (result.size != RESULT_CASE_COUNT) {
            throw SerializationException("Component results must contain exactly one case")
        }

        val (case, element) = result.entries.single()
        val payload = if (element is JsonNull) {
            null
        } else {
            jsonDecoder.json.decodeFromJsonElement(Value.serializer(), element)
        }

        return when (case) {
            OK_CASE -> ComponentResult.Ok(payload)
            ERR_CASE -> ComponentResult.Err(payload)
            else -> throw SerializationException("Unknown component result case: $case")
        }
    }

    override fun serialize(encoder: Encoder, value: ComponentResult) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("Component results require JSON output")
        val (case, payload) = when (value) {
            is ComponentResult.Ok -> OK_CASE to value.payload
            is ComponentResult.Err -> ERR_CASE to value.payload
        }
        val element = payload?.let {
            jsonEncoder.json.encodeToJsonElement(Value.serializer(), it)
        } ?: JsonNull

        jsonEncoder.encodeJsonElement(JsonObject(mapOf(case to element)))
    }

    private const val RESULT_CASE_COUNT = 1
    private const val OK_CASE = "Ok"
    private const val ERR_CASE = "Err"
}
