package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.NumberType
import io.github.charlietap.chasm.ir.type.ReferenceType
import io.github.charlietap.chasm.ir.type.ValueType
import io.github.charlietap.chasm.ir.type.VectorType
import io.github.charlietap.chasm.type.ir.matching.TypeMatcherContext

fun ValueType.default(
    context: TypeMatcherContext,
): Long = when (this) {
    is ValueType.Number -> default()
    is ValueType.Reference -> default(context)
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

fun ValueType.Reference.default(
    context: TypeMatcherContext,
): Long = when (val refType = this.referenceType) {
    is ReferenceType.RefNull -> {
        val substituted = when (val heapType = refType.heapType) {
            is ConcreteHeapType.Defined -> {
                val typeIndex = context.reverseLookup(heapType.definedType)
                ConcreteHeapType.TypeIndex(typeIndex)
            }
            else -> heapType
        }

        ReferenceValue.Null(substituted).toLongFromBoxed()
    }
    is ReferenceType.Ref -> ExecutionValue.Uninitialised.toLongFromBoxed()
}

fun ValueType.Vector.default(): Long = when (this.vectorType) {
    VectorType.V128 -> 0L
}
