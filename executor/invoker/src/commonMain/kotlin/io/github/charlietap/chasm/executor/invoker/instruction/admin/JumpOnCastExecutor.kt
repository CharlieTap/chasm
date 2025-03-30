package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun JumpOnCastExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCast,
): InstructionPointer = JumpOnCastExecutor(
    ip = ip,
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun JumpOnCastExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCast,
    caster: Caster,
): InstructionPointer {

    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val casted = caster(vstack.peek(), instruction.dstReferenceType, moduleInstance, store)
    return if (casted) {
        vstack.shrink(instruction.adjustment.keep, instruction.adjustment.depth)
        ip + instruction.offset
    } else {
        ip + 1
    }
}
