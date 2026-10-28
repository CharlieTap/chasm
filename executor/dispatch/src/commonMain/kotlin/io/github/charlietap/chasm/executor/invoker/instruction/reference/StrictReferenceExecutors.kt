package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.operations.Caster
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

internal inline fun executeRefEq(
    vstack: ValueStack,
    reference1: Long,
    reference2: Long,
    destinationSlot: Int,
) {
    val bothTypesAreNull = reference1.isNullableReference() && reference2.isNullableReference()
    if (bothTypesAreNull || reference1 == reference2) {
        vstack.setFrameSlot(destinationSlot, 1L)
    } else {
        vstack.setFrameSlot(destinationSlot, 0L)
    }
}

internal inline fun executeRefIsNull(
    vstack: ValueStack,
    value: Long,
    destinationSlot: Int,
) {
    if (value.isNullableReference()) {
        vstack.setFrameSlot(destinationSlot, 1L)
    } else {
        vstack.setFrameSlot(destinationSlot, 0L)
    }
}

internal fun RefTestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    referenceValue: Long,
    typeTest: ReferenceTypeTest,
    destinationSlot: Int,
) = RefTestExecutor(
    vstack = vstack,
    context = context,
    referenceValue = referenceValue,
    typeTest = typeTest,
    destinationSlot = destinationSlot,
    caster = ::Caster,
)

internal inline fun RefTestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    referenceValue: Long,
    typeTest: ReferenceTypeTest,
    destinationSlot: Int,
    crossinline caster: Caster,
) {
    if (caster(referenceValue, typeTest, context)) {
        vstack.setFrameSlot(destinationSlot, 1L)
    } else {
        vstack.setFrameSlot(destinationSlot, 0L)
    }
}

internal fun RefCastExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    referenceValue: Long,
    typeTest: ReferenceTypeTest,
    destinationSlot: Int,
) = RefCastExecutor(
    vstack = vstack,
    context = context,
    referenceValue = referenceValue,
    typeTest = typeTest,
    destinationSlot = destinationSlot,
    caster = ::Caster,
)

internal inline fun RefCastExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    referenceValue: Long,
    typeTest: ReferenceTypeTest,
    destinationSlot: Int,
    crossinline caster: Caster,
) {
    if (caster(referenceValue, typeTest, context)) {
        vstack.setFrameSlot(destinationSlot, referenceValue)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
