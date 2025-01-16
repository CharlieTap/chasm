package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

fun ValueType.default(): ExecutionValue = when (this) {
    is ValueType.Number -> default()
    is ValueType.Reference -> default()
    is ValueType.Vector -> default()
    is ValueType.Bottom -> throw InvocationException(InvocationError.UndefinedDefaultBottomType)
}

fun ValueType.Number.default(): ExecutionValue = when (this.numberType) {
    NumberType.I32 -> NumberValue.I32(0)
    NumberType.I64 -> NumberValue.I64(0)
    NumberType.F32 -> NumberValue.F32(0f)
    NumberType.F64 -> NumberValue.F64(0.0)
}

fun ValueType.Reference.default(): ExecutionValue = when (val refType = this.referenceType) {
    is ReferenceType.RefNull -> ReferenceValue.Null(refType.heapType)
    is ReferenceType.Ref -> ExecutionValue.Uninitialised
}

fun ValueType.Vector.default(): ExecutionValue = when (this.vectorType) {
    VectorType.V128 -> VectorValue.V128(ByteArray(16) { 0 })
}
