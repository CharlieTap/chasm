package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.sweet.lib.value.Value

typealias ValueMapper = (Value) -> ExecutionValue?

fun ValueMapper(
    value: Value,
): ExecutionValue? = when (value) {
    is Value.I32 -> i32ValueMapper(value)
    is Value.I64 -> i64ValueMapper(value)
    is Value.F32 -> f32ValueMapper(value)
    is Value.F64 -> f64ValueMapper(value)
    is Value.Extern -> externRefValueMapper(value)
    is Value.Func -> funcRefValueMapper(value)
}

private fun i32ValueMapper(
    value: Value.I32,
): ExecutionValue? = value.value?.let {
    NumberValue.I32(it.toIntOrNull() ?: it.toUInt().toInt())
}

private fun i64ValueMapper(
    value: Value.I64,
): ExecutionValue? = value.value?.let {
    NumberValue.I64(it.toLongOrNull() ?: it.toULong().toLong())
}

private fun f32ValueMapper(
    value: Value.F32,
): ExecutionValue? = value.value?.let {
    if (it.contains("nan")) {
        Float.NaN
    } else {
        val bitPattern = it.toUInt().toInt()
        Float.fromBits(bitPattern)
    }.let(::F32)
}

private fun f64ValueMapper(
    value: Value.F64,
): ExecutionValue? = value.value?.let {
    if (it.contains("nan")) {
        Double.NaN
    } else {
        val bitPattern = it.toULong().toLong()
        Double.fromBits(bitPattern)
    }.let(::F64)
}

private fun externRefValueMapper(
    value: Value.Extern,
): ExecutionValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Extern(ReferenceValue.Host(Address.Host(it)))
} ?: ReferenceValue.Null(AbstractHeapType.Extern)

private fun funcRefValueMapper(
    value: Value.Func,
): ExecutionValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Function(Address.Function(it))
} ?: ReferenceValue.Null(AbstractHeapType.Func)

private fun coalesceNullableInt(value: String) = if (value == "null") {
    null
} else {
    value.toIntOrNull()
}
