package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun RefTestExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefTest,
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
    instruction: ReferenceInstruction.RefTest,
    crossinline caster: Caster,
): InstructionPointer {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    if (caster(vstack.pop(), instruction.referenceType, moduleInstance, store)) {
        vstack.push(1L)
    } else {
        vstack.push(0L)
    }
    return ip + 1
}
