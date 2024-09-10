package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.sweet.lib.value.Value
import io.github.charlietap.chasm.embedding.shapes.Value as ChasmValue

typealias ValueMapper = (Value) -> ChasmValue?

fun ValueMapper(
    value: Value,
): ChasmValue? = when (value) {
    is Value.I32 -> i32ValueMapper(value)
    is Value.I64 -> i64ValueMapper(value)
    is Value.F32 -> f32ValueMapper(value)
    is Value.F64 -> f64ValueMapper(value)
    is Value.Exception -> exceptionRefValueMapper(value)
    is Value.Extern -> externRefValueMapper(value)
    is Value.Func -> funcRefValueMapper(value)
}

private fun i32ValueMapper(
    value: Value.I32,
): ChasmValue.Number.I32? = value.value?.let {
    ChasmValue.Number.I32(it.toIntOrNull() ?: it.toUInt().toInt())
}

private fun i64ValueMapper(
    value: Value.I64,
): ChasmValue.Number.I64? = value.value?.let {
    ChasmValue.Number.I64(it.toLongOrNull() ?: it.toULong().toLong())
}

private fun f32ValueMapper(
    value: Value.F32,
): ChasmValue.Number.F32? = value.value?.let {
    if (it.contains("nan")) {
        Float.NaN
    } else {
        val bitPattern = it.toUInt().toInt()
        Float.fromBits(bitPattern)
    }.let { ChasmValue.Number.F32(it) }
}

private fun f64ValueMapper(
    value: Value.F64,
): ChasmValue.Number.F64? = value.value?.let {
    if (it.contains("nan")) {
        Double.NaN
    } else {
        val bitPattern = it.toULong().toLong()
        Double.fromBits(bitPattern)
    }.let { ChasmValue.Number.F64(it) }
}

private fun exceptionRefValueMapper(
    value: Value.Exception,
): ChasmValue.Reference = value.value?.let {
    ChasmValue.Reference.Null(HeapType.Exception)
} ?: ChasmValue.Reference.Null(HeapType.Exception)

private fun externRefValueMapper(
    value: Value.Extern,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Extern(ChasmValue.Reference.Host(it))
} ?: ChasmValue.Reference.Null(HeapType.Extern)

private fun funcRefValueMapper(
    value: Value.Func,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Func(it)
} ?: ChasmValue.Reference.Null(HeapType.Func)

private fun coalesceNullableInt(value: String) = if (value == "null") {
    null
} else {
    value.toIntOrNull()
}
