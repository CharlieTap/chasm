package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ValueType.default(): Long = when (this) {
    is ValueType.Number -> default()
    is ValueType.Reference -> default()
    is ValueType.Vector -> default()
    is ValueType.Bottom -> throw InvocationException(InvocationError.UndefinedDefaultBottomType)
}

fun ValueType.Number.default(): Long = when (this.numberType) {
    NumberType.I32,
    NumberType.I64,
    NumberType.F32,
    NumberType.F64,
    -> 0L
}

fun ValueType.Reference.default(): Long = when (val refType = this.referenceType) {
    is ReferenceType.RefNull -> ReferenceValue.Null(refType.heapType).toLong()
    is ReferenceType.Ref -> ExecutionValue.Uninitialised.toLong()
}

fun ValueType.Vector.default(): Long = when (this.vectorType) {
    VectorType.V128 -> 0L
}
