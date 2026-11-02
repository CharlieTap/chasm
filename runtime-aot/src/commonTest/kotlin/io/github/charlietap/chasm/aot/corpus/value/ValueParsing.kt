package io.github.charlietap.chasm.aot.corpus.value

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.corpus.lib.fixture.FixtureImport
import io.github.charlietap.corpus.lib.fixture.FixtureValue
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

internal fun parseValue(
    value: JsonObject,
    captures: Map<String, ExecutionValue>,
    expectedType: ValueType? = null,
): ExecutionValue {
    value["var"]?.jsonPrimitive?.contentOrNull?.let { name ->
        return captures.getValue(name)
    }
    val raw = value["value"]?.jsonPrimitive?.contentOrNull

    return when (value.string("type")) {
        "i32" -> {
            val parsed = raw?.let { it.toIntOrNull() ?: it.toUInt().toInt() } ?: 0
            if (expectedType is ValueType.Reference) {
                ReferenceValue.I31(parsed.toUInt())
            } else {
                NumberValue.I32(parsed)
            }
        }
        "i64" -> NumberValue.I64(raw?.let { it.toLongOrNull() ?: it.toULong().toLong() } ?: 0)
        "f32" -> NumberValue.F32(raw?.let { Float.fromBits(it.toUInt().toInt()) } ?: 0f)
        "f64" -> NumberValue.F64(raw?.let { Double.fromBits(it.toULong().toLong()) } ?: 0.0)
        "funcref" -> ReferenceValue.Null(AbstractHeapType.Func)
        "externref" -> ReferenceValue.Null(AbstractHeapType.Extern)
        else -> ExecutionValue.Uninitialised
    }
}

internal fun parseFixtureValue(
    value: FixtureValue,
): ExecutionValue = parseValue(
    JsonObject(
        buildMap {
            put("type", JsonPrimitive(value.type))
            value.value?.let { put("value", JsonPrimitive(it)) }
            value.capture?.let { put("capture", JsonPrimitive(it)) }
            value.variable?.let { put("var", JsonPrimitive(it)) }
        },
    ),
    emptyMap(),
)

internal fun defaultResults(
    type: FunctionType,
    import: FixtureImport?,
): List<ExecutionValue> {
    val declared = import?.stub?.returns.orEmpty()
    return type.results.types.mapIndexed { index, valueType ->
        declared.getOrNull(index)?.let(::parseFixtureValue) ?: defaultValue(valueType)
    }
}

internal fun defaultValue(
    type: ValueType,
): ExecutionValue = when (type) {
    is ValueType.Number -> when (type.numberType.name) {
        "I32" -> NumberValue.I32(0)
        "I64" -> NumberValue.I64(0)
        "F32" -> NumberValue.F32(0f)
        "F64" -> NumberValue.F64(0.0)
        else -> NumberValue.I32(0)
    }
    is ValueType.Reference -> ReferenceValue.Null(type.referenceType.heapType)
    else -> ExecutionValue.Uninitialised
}

internal fun ExecutionValue.describeBits(): String = "bits=${toLongFromBoxed()}"
