package io.github.charlietap.chasm.corpus.value

import io.github.charlietap.chasm.corpus.ext.jsonPrimitiveOrNull
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun parseIndex(
    value: JsonElement,
    captures: Map<String, ExecutionValue>,
): Int {
    value.jsonPrimitiveOrNull()?.intOrNull?.let { index -> return index }
    val variable = value.jsonObject["var"]?.jsonPrimitive?.content ?: error("Invalid corpus index expression")
    return captures.getValue(variable).bits().toInt()
}
