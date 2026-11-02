package io.github.charlietap.chasm.aot.corpus.value

import io.github.charlietap.chasm.aot.corpus.ext.jsonPrimitiveOrNull
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun parseIndex(
    value: JsonElement,
    captures: Map<String, ExecutionValue>,
): Int {
    value.jsonPrimitiveOrNull()?.intOrNull?.let { return it }
    val variable = value.jsonObject["var"]?.jsonPrimitive?.content ?: error("Invalid corpus index expression")
    return captures.getValue(variable).toLongFromBoxed().toInt()
}
