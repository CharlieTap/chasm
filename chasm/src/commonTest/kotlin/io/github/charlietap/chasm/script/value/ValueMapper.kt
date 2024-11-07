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

private fun anyRefValueMapper(
    value: Value.AnyRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Host(it)
} ?: ChasmValue.Reference.Null(HeapType.Any)

private fun arrayRefValueMapper(
    value: Value.ArrayRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.Array)
} ?: ChasmValue.Reference.Null(HeapType.Array)

private fun bottomRefValueMapper(
    value: Value.BottomRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.Bottom)
} ?: ChasmValue.Reference.Null(HeapType.Bottom)

private fun funcRefValueMapper(
    value: Value.FuncRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Func(it)
} ?: ChasmValue.Reference.Null(HeapType.Func)

private fun eqRefValueMapper(
    value: Value.EqRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.Eq)
} ?: ChasmValue.Reference.Null(HeapType.Eq)

private fun exceptionRefValueMapper(
    value: Value.ExceptionRef,
): ChasmValue.Reference = value.value?.let {
    ChasmValue.Reference.Null(HeapType.Exception)
} ?: ChasmValue.Reference.Null(HeapType.Exception)

private fun externRefValueMapper(
    value: Value.ExternRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Extern(ChasmValue.Reference.Host(it))
} ?: ChasmValue.Reference.Null(HeapType.Extern)

private fun i31RefValueMapper(
    value: Value.I31Ref,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.I31)
} ?: ChasmValue.Reference.Null(HeapType.I31)

private fun nullFuncRefValueMapper(
    value: Value.NullFuncRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.NoFunc)
} ?: ChasmValue.Reference.Null(HeapType.NoFunc)

private fun nullExceptionRefValueMapper(
    value: Value.NullExceptionRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.NoException)
} ?: ChasmValue.Reference.Null(HeapType.NoException)

private fun nullExternRefValueMapper(
    value: Value.NullExternRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.NoExtern)
} ?: ChasmValue.Reference.Null(HeapType.NoExtern)

private fun nullRefValueMapper(
    value: Value.NullRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.None)
} ?: ChasmValue.Reference.Null(HeapType.None)

private fun structRefValueMapper(
    value: Value.StructRef,
): ChasmValue.Reference = value.value?.let(::coalesceNullableInt)?.let {
    ChasmValue.Reference.Null(HeapType.Struct)
} ?: ChasmValue.Reference.Null(HeapType.Struct)

private fun coalesceNullableInt(value: String) = if (value == "null") {
    null
} else {
    value.toIntOrNull()
}
