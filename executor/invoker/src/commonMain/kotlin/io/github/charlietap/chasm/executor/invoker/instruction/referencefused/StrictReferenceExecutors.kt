package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType

internal inline fun RefEqExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefEqSs,
) = executeRefEq(
    vstack = vstack,
    reference1 = vstack.getFrameSlot(instruction.reference1Slot),
    reference2 = vstack.getFrameSlot(instruction.reference2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun RefIsNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefIsNullS,
) = executeRefIsNull(
    vstack = vstack,
    value = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun RefAsNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefAsNonNullS,
) {
    val value = vstack.getFrameSlot(instruction.valueSlot)
    if (value.isNullableReference()) {
        throw InvocationException(InvocationError.NonNullReferenceExpected)
    }
    vstack.setFrameSlot(instruction.destinationSlot, value)
}

internal inline fun RefNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefNullS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.reference)
}

internal inline fun RefFuncExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefFuncS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.reference)
}

internal fun RefTestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefTestS,
) = RefTestExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefTestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefTestS,
    crossinline caster: Caster,
) = executeRefTest(
    vstack = vstack,
    cstack = cstack,
    store = store,
    referenceValue = vstack.getFrameSlot(instruction.referenceSlot),
    referenceType = instruction.referenceType,
    destinationSlot = instruction.destinationSlot,
    caster = caster,
)

internal fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefCastS,
) = RefCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceSuperInstruction.RefCastS,
    crossinline caster: Caster,
) = executeRefCast(
    vstack = vstack,
    cstack = cstack,
    store = store,
    referenceValue = vstack.getFrameSlot(instruction.referenceSlot),
    referenceType = instruction.referenceType,
    destinationSlot = instruction.destinationSlot,
    caster = caster,
)

private inline fun executeRefEq(
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

private inline fun executeRefIsNull(
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

private inline fun executeRefTest(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    referenceValue: Long,
    referenceType: ReferenceType,
    destinationSlot: Int,
    crossinline caster: Caster,
) {
    val moduleInstance = cstack.peekFrame().instance
    if (caster(referenceValue, referenceType, moduleInstance, store)) {
        vstack.setFrameSlot(destinationSlot, 1L)
    } else {
        vstack.setFrameSlot(destinationSlot, 0L)
    }
}

private inline fun executeRefCast(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    referenceValue: Long,
    referenceType: ReferenceType,
    destinationSlot: Int,
    crossinline caster: Caster,
) {
    val moduleInstance = cstack.peekFrame().instance
    if (caster(referenceValue, referenceType, moduleInstance, store)) {
        vstack.setFrameSlot(destinationSlot, referenceValue)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
