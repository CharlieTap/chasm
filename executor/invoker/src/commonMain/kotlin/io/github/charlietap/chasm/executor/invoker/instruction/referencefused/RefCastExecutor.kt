package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun RefCastExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefCast,
): InstructionPointer = RefCastExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefCastExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefCast,
    crossinline caster: Caster,
): InstructionPointer {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = instruction.reference(vstack)
    if (caster(referenceValue, instruction.referenceType, moduleInstance, store)) {
        instruction.destination(referenceValue, vstack)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
    return ip + 1
}
