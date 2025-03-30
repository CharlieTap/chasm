package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun RefTestExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
): InstructionPointer = RefTestExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefTestExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
    crossinline caster: Caster,
): InstructionPointer {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    if (caster(instruction.reference(vstack), instruction.referenceType, moduleInstance, store)) {
        instruction.destination(1L, vstack)
    } else {
        instruction.destination(0L, vstack)
    }
    return ip + 1
}
