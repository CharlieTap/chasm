package io.github.charlietap.chasm.aot.corpus.ext

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

internal fun JsonObject.string(
    name: String,
): String = getValue(name).jsonPrimitive.content

internal fun JsonObject.stringOrDefault(
    name: String,
    default: String,
): String = this[name]?.jsonPrimitive?.contentOrNull ?: default

internal fun JsonObject.array(
    name: String,
): JsonArray = arrayOrNull(name) ?: JsonArray(emptyList())

internal fun JsonObject.arrayOrNull(
    name: String,
): JsonArray? = this[name]?.jsonArray

internal fun JsonElement.jsonPrimitiveOrNull(): JsonPrimitive? = this as? JsonPrimitive
