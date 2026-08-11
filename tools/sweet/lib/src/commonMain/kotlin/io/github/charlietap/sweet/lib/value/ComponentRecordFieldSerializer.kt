package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

object ComponentRecordFieldSerializer : KSerializer<ComponentRecordField> {

    override val descriptor: SerialDescriptor =
        ListSerializer(JsonElement.serializer()).descriptor

    override fun deserialize(decoder: Decoder): ComponentRecordField {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("Component record fields require JSON input")
        val elements = jsonDecoder.decodeJsonElement() as? JsonArray
            ?: throw SerializationException("Component record fields must be two-element arrays")

        if (elements.size != RECORD_FIELD_SIZE || !elements[0].jsonPrimitive.isString) {
            throw SerializationException("Component record fields must contain a name and value")
        }

        return ComponentRecordField(
            name = elements[0].jsonPrimitive.content,
            value = jsonDecoder.json.decodeFromJsonElement(Value.serializer(), elements[1]),
        )
    }

    override fun serialize(encoder: Encoder, value: ComponentRecordField) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("Component record fields require JSON output")

        jsonEncoder.encodeJsonElement(
            JsonArray(
                listOf(
                    JsonPrimitive(value.name),
                    jsonEncoder.json.encodeToJsonElement(Value.serializer(), value.value),
                ),
            ),
        )
    }

    private const val RECORD_FIELD_SIZE = 2
}
