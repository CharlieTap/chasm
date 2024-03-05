package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

internal fun ValueType.default(): ExecutionValue = when (this) {
    is ValueType.Number -> default()
    is ValueType.Reference -> default()
    is ValueType.Vector -> default()
}

internal fun ValueType.Number.default(): ExecutionValue = when (this.numberType) {
    NumberType.I32 -> NumberValue.I32(0)
    NumberType.I64 -> NumberValue.I64(0)
    NumberType.F32 -> NumberValue.F32(0f)
    NumberType.F64 -> NumberValue.F64(0.0)
}

internal fun ValueType.Reference.default(): ExecutionValue = when (val refType = this.referenceType) {
    ReferenceType.Funcref -> ReferenceValue.Null(refType)
    ReferenceType.Externref -> ReferenceValue.Null(refType)
}

internal fun ValueType.Vector.default(): ExecutionValue = when (this.vectorType) {
    VectorType.V128 -> VectorValue.V128(ByteArray(16) { 0 })
}
