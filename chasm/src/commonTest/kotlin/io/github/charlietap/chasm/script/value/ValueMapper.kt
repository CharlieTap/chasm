package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.runtime.value.VectorValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.BottomType
import io.github.charlietap.sweet.lib.value.Value

typealias ValueMapper = (Value) -> ExecutionValue?

fun ValueMapper(
    value: Value,
): ExecutionValue? = when (value) {
    is Value.I32 -> i32ValueMapper(value)
    is Value.I64 -> i64ValueMapper(value)
    is Value.F32 -> f32ValueMapper(value)
    is Value.F64 -> f64ValueMapper(value)
    is Value.AnyRef -> anyRefValueMapper(value)
    is Value.ArrayRef -> arrayRefValueMapper(value)
    is Value.BottomRef -> bottomRefValueMapper(value)
    is Value.EqRef -> eqRefValueMapper(value)
    is Value.ExceptionRef -> exceptionRefValueMapper(value)
    is Value.ExternRef -> externRefValueMapper(value)
    is Value.FuncRef -> funcRefValueMapper(value)
    is Value.I31Ref -> i31RefValueMapper(value)
    is Value.NullFuncRef -> nullFuncRefValueMapper(value)
    is Value.NullExceptionRef -> nullExceptionRefValueMapper(value)
    is Value.NullExternRef -> nullExternRefValueMapper(value)
    is Value.NullRef -> nullRefValueMapper(value)
    is Value.StructRef -> structRefValueMapper(value)
    is Value.V128 -> v128ValueMapper(value)
}

private fun i32ValueMapper(
    value: Value.I32,
): NumberValue.I32? = value.value?.let {
    NumberValue.I32(it.toIntOrNull() ?: it.toUInt().toInt())
}

private fun i64ValueMapper(
    value: Value.I64,
): NumberValue.I64? = value.value?.let {
    NumberValue.I64(it.toLongOrNull() ?: it.toULong().toLong())
}

private fun f32ValueMapper(
    value: Value.F32,
): NumberValue.F32? = value.value?.let {
    if (it.contains("nan")) {
        Float.NaN
    } else {
        val bitPattern = it.toUInt().toInt()
        Float.fromBits(bitPattern)
    }.let { NumberValue.F32(it) }
}

private fun f64ValueMapper(
    value: Value.F64,
): NumberValue.F64? = value.value?.let {
    if (it.contains("nan")) {
        Double.NaN
    } else {
        val bitPattern = it.toULong().toLong()
        Double.fromBits(bitPattern)
    }.let { NumberValue.F64(it) }
}

private fun anyRefValueMapper(
    value: Value.AnyRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Host(Address.Host(it))
} ?: ReferenceValue.Null(AbstractHeapType.Any)

private fun arrayRefValueMapper(
    value: Value.ArrayRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.Array)
} ?: ReferenceValue.Null(AbstractHeapType.Array)

private fun bottomRefValueMapper(
    value: Value.BottomRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.Bottom(BottomType))
} ?: ReferenceValue.Null(AbstractHeapType.Bottom(BottomType))

private fun funcRefValueMapper(
    value: Value.FuncRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Function(Address.Function(it))
} ?: ReferenceValue.Null(AbstractHeapType.Func)

private fun eqRefValueMapper(
    value: Value.EqRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.Eq)
} ?: ReferenceValue.Null(AbstractHeapType.Eq)

private fun exceptionRefValueMapper(
    value: Value.ExceptionRef,
): ReferenceValue = value.value?.let {
    ReferenceValue.Null(AbstractHeapType.Exception)
} ?: ReferenceValue.Null(AbstractHeapType.Exception)

private fun externRefValueMapper(
    value: Value.ExternRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Extern(ReferenceValue.Host(Address.Host(it)))
} ?: ReferenceValue.Null(AbstractHeapType.Extern)

private fun i31RefValueMapper(
    value: Value.I31Ref,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.I31)
} ?: ReferenceValue.Null(AbstractHeapType.I31)

private fun nullFuncRefValueMapper(
    value: Value.NullFuncRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.NoFunc)
} ?: ReferenceValue.Null(AbstractHeapType.NoFunc)

private fun nullExceptionRefValueMapper(
    value: Value.NullExceptionRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.NoException)
} ?: ReferenceValue.Null(AbstractHeapType.NoException)

private fun nullExternRefValueMapper(
    value: Value.NullExternRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.NoExtern)
} ?: ReferenceValue.Null(AbstractHeapType.NoExtern)

private fun nullRefValueMapper(
    value: Value.NullRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.None)
} ?: ReferenceValue.Null(AbstractHeapType.None)

private fun structRefValueMapper(
    value: Value.StructRef,
): ReferenceValue = value.value?.let(::coalesceNullableInt)?.let {
    ReferenceValue.Null(AbstractHeapType.Struct)
} ?: ReferenceValue.Null(AbstractHeapType.Struct)

private fun coalesceNullableInt(value: String) = if (value == "null") {
    null
} else {
    value.toIntOrNull()
}

private fun v128ValueMapper(
    value: Value.V128,
): VectorValue.V128 {
    val bytes = ByteArray(16)
    when (value.laneType) {
        "i8" -> {
            value.value.forEachIndexed { i, v ->
                bytes[i] = (v.toIntOrNull() ?: v.toUInt().toInt()).toByte()
            }
        }
        "i16" -> {
            value.value.forEachIndexed { i, v ->
                val short = (v.toIntOrNull() ?: v.toUInt().toInt()).toShort()
                bytes[i * 2] = short.toByte()
                bytes[i * 2 + 1] = (short.toInt() shr 8).toByte()
            }
        }
        "i32" -> {
            value.value.forEachIndexed { i, v ->
                val int = v.toIntOrNull() ?: v.toUInt().toInt()
                bytes[i * 4] = int.toByte()
                bytes[i * 4 + 1] = (int shr 8).toByte()
                bytes[i * 4 + 2] = (int shr 16).toByte()
                bytes[i * 4 + 3] = (int shr 24).toByte()
            }
        }
        "i64" -> {
            value.value.forEachIndexed { i, v ->
                val long = v.toLongOrNull() ?: v.toULong().toLong()
                bytes[i * 8] = long.toByte()
                bytes[i * 8 + 1] = (long shr 8).toByte()
                bytes[i * 8 + 2] = (long shr 16).toByte()
                bytes[i * 8 + 3] = (long shr 24).toByte()
                bytes[i * 8 + 4] = (long shr 32).toByte()
                bytes[i * 8 + 5] = (long shr 40).toByte()
                bytes[i * 8 + 6] = (long shr 48).toByte()
                bytes[i * 8 + 7] = (long shr 56).toByte()
            }
        }
        "f32" -> {
            value.value.forEachIndexed { i, v ->
                val int = if (v.contains("nan")) {
                    Float.NaN.toRawBits()
                } else {
                    v.toUInt().toInt()
                }
                bytes[i * 4] = int.toByte()
                bytes[i * 4 + 1] = (int shr 8).toByte()
                bytes[i * 4 + 2] = (int shr 16).toByte()
                bytes[i * 4 + 3] = (int shr 24).toByte()
            }
        }
        "f64" -> {
            value.value.forEachIndexed { i, v ->
                val long = if (v.contains("nan")) {
                    Double.NaN.toRawBits()
                } else {
                    v.toULong().toLong()
                }
                bytes[i * 8] = long.toByte()
                bytes[i * 8 + 1] = (long shr 8).toByte()
                bytes[i * 8 + 2] = (long shr 16).toByte()
                bytes[i * 8 + 3] = (long shr 24).toByte()
                bytes[i * 8 + 4] = (long shr 32).toByte()
                bytes[i * 8 + 5] = (long shr 40).toByte()
                bytes[i * 8 + 6] = (long shr 48).toByte()
                bytes[i * 8 + 7] = (long shr 56).toByte()
            }
        }
    }
    return VectorValue.V128(bytes)
}
