package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, _ ->
    instruction.targetIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfI,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, nextIp ->
    if (instruction.operand != 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfS,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.getFrameSlot(instruction.operandSlot) != 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfV,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.pop() != 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableI,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, _ ->
    instruction.targetIps.getOrElse(instruction.operand) { instruction.defaultTargetIp }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableS,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, _ ->
    val operand = vstack.getFrameSlot(instruction.operandSlot).toInt()
    instruction.targetIps.getOrElse(operand) { instruction.defaultTargetIp }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableV,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, _ ->
    instruction.targetIps.getOrElse(vstack.popI32()) { instruction.defaultTargetIp }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNullI,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, nextIp ->
    if (instruction.operand.isNullableReference()) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNullS,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.getFrameSlot(instruction.operandSlot).isNullableReference()) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNullV,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.peek().isNullableReference()) {
        vstack.pop()
        instruction.targetIp
    } else {
        nextIp
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNonNullI,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, nextIp ->
    if (!instruction.operand.isNullableReference()) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNonNullS,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (!vstack.getFrameSlot(instruction.operandSlot).isNullableReference()) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnNonNullV,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    val operand = vstack.pop()
    if (!operand.isNullableReference()) {
        vstack.push(operand)
        instruction.targetIp
    } else {
        nextIp
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastI,
): DispatchableInstruction = DispatchableInstruction { _, cstack, store, _, nextIp ->
    val matches = Caster(instruction.operand, instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (matches) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, nextIp ->
    val operand = vstack.getFrameSlot(instruction.operandSlot)
    val matches = Caster(operand, instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (matches) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastV,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, nextIp ->
    val matches = Caster(vstack.peek(), instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (matches) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastFailI,
): DispatchableInstruction = DispatchableInstruction { _, cstack, store, _, nextIp ->
    val matches = Caster(instruction.operand, instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (!matches) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastFailS,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, nextIp ->
    val operand = vstack.getFrameSlot(instruction.operandSlot)
    val matches = Caster(operand, instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (!matches) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpOnCastFailV,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, store, _, nextIp ->
    val matches = Caster(vstack.peek(), instruction.dstReferenceType, cstack.peekFrame().instance, store)
    if (!matches) instruction.targetIp else nextIp
}
