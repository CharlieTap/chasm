package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.function.copyOperands
import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.NumericCondition

fun JumpDispatcher(
    instruction: AdminInstruction.Jump,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, _ ->
    instruction.targetIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpCopies,
): DispatchableInstruction {
    val destinationSlotBase = instruction.destinationSlotBase
    val targetIp = instruction.targetIp
    val operands = instruction.operands
    val operand = operands.operands.singleOrNull()
    return when (operand) {
        is CopyOperand.Immediate -> {
            val value = operand.value
            DispatchableInstruction { vstack, _, _, _, _ ->
                vstack.setFrameSlot(destinationSlotBase, value)
                targetIp
            }
        }
        is CopyOperand.Slot -> {
            val sourceSlot = operand.slot
            DispatchableInstruction { vstack, _, _, _, _ ->
                vstack.setFrameSlot(destinationSlotBase, vstack.getFrameSlot(sourceSlot))
                targetIp
            }
        }
        null -> DispatchableInstruction { vstack, _, _, _, _ ->
            val framePointer = vstack.framePointer
            copyOperands(
                vstack = vstack,
                currentFramePointer = framePointer,
                destinationFramePointer = framePointer + destinationSlotBase,
                operands = operands.operands,
                order = operands.order,
            )
            targetIp
        }
    }
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
    instruction: AdminInstruction.JumpIfZeroI,
): DispatchableInstruction = DispatchableInstruction { _, _, _, _, nextIp ->
    if (instruction.operand == 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfZeroS,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.getFrameSlot(instruction.operandSlot) == 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfV,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
    if (vstack.pop() != 0L) instruction.targetIp else nextIp
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfCopyI,
): DispatchableInstruction = if (instruction.operand != 0L) {
    val sourceSlot = instruction.sourceSlot
    val destinationSlot = instruction.destinationSlot
    val targetIp = instruction.targetIp
    DispatchableInstruction { vstack, _, _, _, _ ->
        vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
        targetIp
    }
} else {
    DispatchableInstruction { _, _, _, _, nextIp -> nextIp }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfCopyS,
): DispatchableInstruction {
    val operandSlot = instruction.operandSlot
    val sourceSlot = instruction.sourceSlot
    val destinationSlot = instruction.destinationSlot
    val targetIp = instruction.targetIp
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        if (vstack.getFrameSlot(operandSlot) != 0L) {
            vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
            targetIp
        } else {
            nextIp
        }
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfCopyV,
): DispatchableInstruction {
    val sourceSlot = instruction.sourceSlot
    val destinationSlot = instruction.destinationSlot
    val targetIp = instruction.targetIp
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        if (vstack.pop() != 0L) {
            vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
            targetIp
        } else {
            nextIp
        }
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpIfCondition,
): DispatchableInstruction = when (val condition = instruction.condition) {
    is NumericCondition.I32Eqz -> I32ConditionDispatcher(condition.operand, instruction.targetIp) { operand, targetIp, nextIp -> if (operand == 0) targetIp else nextIp }
    is NumericCondition.I32And -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if ((left and right) != 0) targetIp else nextIp }
    is NumericCondition.I64Eqz -> I64ConditionDispatcher(condition.operand, instruction.targetIp) { operand, targetIp, nextIp -> if (operand == 0L) targetIp else nextIp }
    is NumericCondition.I32Eq -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }
    is NumericCondition.I32Ne -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }
    is NumericCondition.I32LtS -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }
    is NumericCondition.I32LtU -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toUInt() < right.toUInt()) targetIp else nextIp }
    is NumericCondition.I32GtS -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }
    is NumericCondition.I32GtU -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toUInt() > right.toUInt()) targetIp else nextIp }
    is NumericCondition.I32LeS -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }
    is NumericCondition.I32LeU -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toUInt() <= right.toUInt()) targetIp else nextIp }
    is NumericCondition.I32GeS -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }
    is NumericCondition.I32GeU -> I32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toUInt() >= right.toUInt()) targetIp else nextIp }
    is NumericCondition.I64Eq -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }
    is NumericCondition.I64Ne -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }
    is NumericCondition.I64LtS -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }
    is NumericCondition.I64LtU -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toULong() < right.toULong()) targetIp else nextIp }
    is NumericCondition.I64GtS -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }
    is NumericCondition.I64GtU -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toULong() > right.toULong()) targetIp else nextIp }
    is NumericCondition.I64LeS -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }
    is NumericCondition.I64LeU -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toULong() <= right.toULong()) targetIp else nextIp }
    is NumericCondition.I64GeS -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }
    is NumericCondition.I64GeU -> I64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left.toULong() >= right.toULong()) targetIp else nextIp }
    is NumericCondition.F32Eq -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }
    is NumericCondition.F32Ne -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }
    is NumericCondition.F32Lt -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }
    is NumericCondition.F32Gt -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }
    is NumericCondition.F32Le -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }
    is NumericCondition.F32Ge -> F32ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }
    is NumericCondition.F64Eq -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }
    is NumericCondition.F64Ne -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }
    is NumericCondition.F64Lt -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }
    is NumericCondition.F64Gt -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }
    is NumericCondition.F64Le -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }
    is NumericCondition.F64Ge -> F64ConditionDispatcher(condition.left, condition.right, instruction.targetIp) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }
}

fun JumpConditionDispatcher(
    condition: NumericCondition,
    targetIp: Int,
    branchOnMatch: Boolean = true,
): DispatchableInstruction = if (branchOnMatch) {
    JumpDispatcher(AdminInstruction.JumpIfCondition(condition, targetIp))
} else {
    JumpConditionMismatchDispatcher(condition, targetIp)
}

private fun JumpConditionMismatchDispatcher(
    condition: NumericCondition,
    targetIp: Int,
): DispatchableInstruction = when (condition) {
    is NumericCondition.I32Eqz -> I32ConditionDispatcher(condition.operand, targetIp) { operand, branchIp, nextIp -> if (operand != 0) branchIp else nextIp }
    is NumericCondition.I32And -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if ((left and right) == 0) branchIp else nextIp }
    is NumericCondition.I64Eqz -> I64ConditionDispatcher(condition.operand, targetIp) { operand, branchIp, nextIp -> if (operand != 0L) branchIp else nextIp }
    is NumericCondition.I32Eq -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }
    is NumericCondition.I32Ne -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }
    is NumericCondition.I32LtS -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left >= right) branchIp else nextIp }
    is NumericCondition.I32LtU -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toUInt() >= right.toUInt()) branchIp else nextIp }
    is NumericCondition.I32GtS -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left <= right) branchIp else nextIp }
    is NumericCondition.I32GtU -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toUInt() <= right.toUInt()) branchIp else nextIp }
    is NumericCondition.I32LeS -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left > right) branchIp else nextIp }
    is NumericCondition.I32LeU -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toUInt() > right.toUInt()) branchIp else nextIp }
    is NumericCondition.I32GeS -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left < right) branchIp else nextIp }
    is NumericCondition.I32GeU -> I32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toUInt() < right.toUInt()) branchIp else nextIp }
    is NumericCondition.I64Eq -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }
    is NumericCondition.I64Ne -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }
    is NumericCondition.I64LtS -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left >= right) branchIp else nextIp }
    is NumericCondition.I64LtU -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toULong() >= right.toULong()) branchIp else nextIp }
    is NumericCondition.I64GtS -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left <= right) branchIp else nextIp }
    is NumericCondition.I64GtU -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toULong() <= right.toULong()) branchIp else nextIp }
    is NumericCondition.I64LeS -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left > right) branchIp else nextIp }
    is NumericCondition.I64LeU -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toULong() > right.toULong()) branchIp else nextIp }
    is NumericCondition.I64GeS -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left < right) branchIp else nextIp }
    is NumericCondition.I64GeU -> I64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left.toULong() < right.toULong()) branchIp else nextIp }
    is NumericCondition.F32Eq -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }
    is NumericCondition.F32Ne -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }
    is NumericCondition.F32Lt -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left < right)) branchIp else nextIp }
    is NumericCondition.F32Gt -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left > right)) branchIp else nextIp }
    is NumericCondition.F32Le -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left <= right)) branchIp else nextIp }
    is NumericCondition.F32Ge -> F32ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left >= right)) branchIp else nextIp }
    is NumericCondition.F64Eq -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }
    is NumericCondition.F64Ne -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }
    is NumericCondition.F64Lt -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left < right)) branchIp else nextIp }
    is NumericCondition.F64Gt -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left > right)) branchIp else nextIp }
    is NumericCondition.F64Le -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left <= right)) branchIp else nextIp }
    is NumericCondition.F64Ge -> F64ConditionDispatcher(condition.left, condition.right, targetIp) { left, right, branchIp, nextIp -> if (!(left >= right)) branchIp else nextIp }
}

private inline fun I32ConditionDispatcher(
    operand: FusedOperand,
    targetIp: Int,
    crossinline branch: (Int, Int, Int) -> Int,
): DispatchableInstruction = when (operand) {
    is FusedOperand.I32Const -> {
        val value = operand.const
        DispatchableInstruction { _, _, _, _, nextIp -> branch(value, targetIp, nextIp) }
    }
    is FusedOperand.FrameSlot -> {
        val slot = operand.offset
        DispatchableInstruction { vstack, _, _, _, nextIp ->
            branch(vstack.getFrameSlot(slot).toInt(), targetIp, nextIp)
        }
    }
    else -> unsupportedConditionOperand(operand)
}

private inline fun I64ConditionDispatcher(
    operand: FusedOperand,
    targetIp: Int,
    crossinline branch: (Long, Int, Int) -> Int,
): DispatchableInstruction = when (operand) {
    is FusedOperand.I64Const -> {
        val value = operand.const
        DispatchableInstruction { _, _, _, _, nextIp -> branch(value, targetIp, nextIp) }
    }
    is FusedOperand.FrameSlot -> {
        val slot = operand.offset
        DispatchableInstruction { vstack, _, _, _, nextIp ->
            branch(vstack.getFrameSlot(slot), targetIp, nextIp)
        }
    }
    else -> unsupportedConditionOperand(operand)
}

private inline fun I32ConditionDispatcher(
    left: FusedOperand,
    right: FusedOperand,
    targetIp: Int,
    crossinline branch: (Int, Int, Int, Int) -> Int,
): DispatchableInstruction = when (left) {
    is FusedOperand.I32Const -> when (right) {
        is FusedOperand.I32Const -> {
            val leftValue = left.const
            val rightValue = right.const
            DispatchableInstruction { _, _, _, _, nextIp -> branch(leftValue, rightValue, targetIp, nextIp) }
        }
        is FusedOperand.FrameSlot -> {
            val leftValue = left.const
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(leftValue, vstack.getFrameSlot(rightSlot).toInt(), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    is FusedOperand.FrameSlot -> when (right) {
        is FusedOperand.I32Const -> {
            val leftSlot = left.offset
            val rightValue = right.const
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot).toInt(), rightValue, targetIp, nextIp)
            }
        }
        is FusedOperand.FrameSlot -> {
            val leftSlot = left.offset
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot).toInt(), vstack.getFrameSlot(rightSlot).toInt(), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    else -> unsupportedConditionOperand(left)
}

private inline fun I64ConditionDispatcher(
    left: FusedOperand,
    right: FusedOperand,
    targetIp: Int,
    crossinline branch: (Long, Long, Int, Int) -> Int,
): DispatchableInstruction = when (left) {
    is FusedOperand.I64Const -> when (right) {
        is FusedOperand.I64Const -> {
            val leftValue = left.const
            val rightValue = right.const
            DispatchableInstruction { _, _, _, _, nextIp -> branch(leftValue, rightValue, targetIp, nextIp) }
        }
        is FusedOperand.FrameSlot -> {
            val leftValue = left.const
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(leftValue, vstack.getFrameSlot(rightSlot), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    is FusedOperand.FrameSlot -> when (right) {
        is FusedOperand.I64Const -> {
            val leftSlot = left.offset
            val rightValue = right.const
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot), rightValue, targetIp, nextIp)
            }
        }
        is FusedOperand.FrameSlot -> {
            val leftSlot = left.offset
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, _, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot), vstack.getFrameSlot(rightSlot), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    else -> unsupportedConditionOperand(left)
}

private inline fun F32ConditionDispatcher(
    left: FusedOperand,
    right: FusedOperand,
    targetIp: Int,
    crossinline branch: (Float, Float, Int, Int) -> Int,
): DispatchableInstruction = I32ConditionDispatcher(
    left = left.f32BitsOperand(),
    right = right.f32BitsOperand(),
    targetIp = targetIp,
) { leftBits, rightBits, branchIp, nextIp ->
    branch(Float.fromBits(leftBits), Float.fromBits(rightBits), branchIp, nextIp)
}

private inline fun F64ConditionDispatcher(
    left: FusedOperand,
    right: FusedOperand,
    targetIp: Int,
    crossinline branch: (Double, Double, Int, Int) -> Int,
): DispatchableInstruction = I64ConditionDispatcher(
    left = left.f64BitsOperand(),
    right = right.f64BitsOperand(),
    targetIp = targetIp,
) { leftBits, rightBits, branchIp, nextIp ->
    branch(Double.fromBits(leftBits), Double.fromBits(rightBits), branchIp, nextIp)
}

private fun unsupportedConditionOperand(operand: FusedOperand): Nothing =
    error("numeric condition must be frame-slot lowered before dispatch: $operand")

private fun FusedOperand.f32BitsOperand(): FusedOperand = when (this) {
    is FusedOperand.F32Const -> FusedOperand.I32Const(const.toRawBits())
    is FusedOperand.FrameSlot -> this
    else -> unsupportedConditionOperand(this)
}

private fun FusedOperand.f64BitsOperand(): FusedOperand = when (this) {
    is FusedOperand.F64Const -> FusedOperand.I64Const(const.toRawBits())
    is FusedOperand.FrameSlot -> this
    else -> unsupportedConditionOperand(this)
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableI,
): DispatchableInstruction {
    val operand = instruction.operand
    val targetIps = instruction.targetIps
    return DispatchableInstruction { _, _, _, _, _ ->
        targetIps.branchTarget(operand)
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableS,
): DispatchableInstruction {
    val operandSlot = instruction.operandSlot
    val targetIps = instruction.targetIps
    return DispatchableInstruction { vstack, _, _, _, _ ->
        targetIps.branchTarget(vstack.getFrameSlot(operandSlot).toInt())
    }
}

fun JumpDispatcher(
    instruction: AdminInstruction.JumpTableV,
): DispatchableInstruction {
    val targetIps = instruction.targetIps
    return DispatchableInstruction { vstack, _, _, _, _ ->
        targetIps.branchTarget(vstack.popI32())
    }
}

private inline fun IntArray.branchTarget(index: Int): Int {
    val defaultIndex = size - 1
    return if (index >= 0 && index < defaultIndex) this[index] else this[defaultIndex]
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
