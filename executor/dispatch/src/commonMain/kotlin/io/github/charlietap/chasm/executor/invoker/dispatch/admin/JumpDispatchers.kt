package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.operations.Caster
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun JumpDispatcher(
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { _, _, _ ->
        targetIp
    }
}

fun JumpCopiesDispatcher(
    operands: OperandTransfer,
    destinationSlotBase: Int,
    targetIp: Int,
): DispatchableInstruction {
    val operand = operands.sources.singleOrNull()
    return when (operand) {
        is TransferSource.Immediate -> {
            val value = operand.value
            DispatchableInstruction { vstack, _, _ ->
                vstack.setFrameSlot(destinationSlotBase, value)
                targetIp
            }
        }
        is TransferSource.Slot -> {
            val sourceSlot = operand.slot
            DispatchableInstruction { vstack, _, _ ->
                vstack.setFrameSlot(destinationSlotBase, vstack.getFrameSlot(sourceSlot))
                targetIp
            }
        }
        null -> DispatchableInstruction { vstack, _, _ ->
            val fp = vstack.fp
            vstack.transferOperands(
                currentFp = fp,
                destinationFp = fp + destinationSlotBase,
                transfer = operands,
            )
            targetIp
        }
    }
}

fun JumpIfIDispatcher(
    operand: Long,
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { _, _, nextIp ->
        if (operand != 0L) targetIp else nextIp
    }
}

fun JumpIfSDispatcher(
    operandSlot: Int,
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        if (vstack.getFrameSlot(operandSlot) != 0L) targetIp else nextIp
    }
}

fun JumpIfZeroIDispatcher(
    operand: Long,
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { _, _, nextIp ->
        if (operand == 0L) targetIp else nextIp
    }
}

fun JumpIfZeroSDispatcher(
    operandSlot: Int,
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        if (vstack.getFrameSlot(operandSlot) == 0L) targetIp else nextIp
    }
}

fun JumpIfCopyIDispatcher(
    operand: Long,
    sourceSlot: Int,
    destinationSlot: Int,
    targetIp: Int,
): DispatchableInstruction = if (operand != 0L) {
    DispatchableInstruction { vstack, _, _ ->
        vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
        targetIp
    }
} else {
    DispatchableInstruction { _, _, nextIp -> nextIp }
}

fun JumpIfCopySDispatcher(
    operandSlot: Int,
    sourceSlot: Int,
    destinationSlot: Int,
    targetIp: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        if (vstack.getFrameSlot(operandSlot) != 0L) {
            vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
            targetIp
        } else {
            nextIp
        }
    }
}

fun JumpIfConditionDispatcher(
    condition: NumericCondition,
    targetIp: Int,
): DispatchableInstruction = when (val condition = condition) {
    is NumericCondition.I32Eqz -> jumpIfI32Eqz(condition, targetIp)
    is NumericCondition.I32And -> jumpIfI32And(condition, targetIp)
    is NumericCondition.I64Eqz -> jumpIfI64Eqz(condition, targetIp)
    is NumericCondition.I32Eq -> jumpIfI32Eq(condition, targetIp)
    is NumericCondition.I32Ne -> jumpIfI32Ne(condition, targetIp)
    is NumericCondition.I32LtS -> jumpIfI32LtS(condition, targetIp)
    is NumericCondition.I32LtU -> jumpIfI32LtU(condition, targetIp)
    is NumericCondition.I32GtS -> jumpIfI32GtS(condition, targetIp)
    is NumericCondition.I32GtU -> jumpIfI32GtU(condition, targetIp)
    is NumericCondition.I32LeS -> jumpIfI32LeS(condition, targetIp)
    is NumericCondition.I32LeU -> jumpIfI32LeU(condition, targetIp)
    is NumericCondition.I32GeS -> jumpIfI32GeS(condition, targetIp)
    is NumericCondition.I32GeU -> jumpIfI32GeU(condition, targetIp)
    is NumericCondition.I64Eq -> jumpIfI64Eq(condition, targetIp)
    is NumericCondition.I64Ne -> jumpIfI64Ne(condition, targetIp)
    is NumericCondition.I64LtS -> jumpIfI64LtS(condition, targetIp)
    is NumericCondition.I64LtU -> jumpIfI64LtU(condition, targetIp)
    is NumericCondition.I64GtS -> jumpIfI64GtS(condition, targetIp)
    is NumericCondition.I64GtU -> jumpIfI64GtU(condition, targetIp)
    is NumericCondition.I64LeS -> jumpIfI64LeS(condition, targetIp)
    is NumericCondition.I64LeU -> jumpIfI64LeU(condition, targetIp)
    is NumericCondition.I64GeS -> jumpIfI64GeS(condition, targetIp)
    is NumericCondition.I64GeU -> jumpIfI64GeU(condition, targetIp)
    is NumericCondition.F32Eq -> jumpIfF32Eq(condition, targetIp)
    is NumericCondition.F32Ne -> jumpIfF32Ne(condition, targetIp)
    is NumericCondition.F32Lt -> jumpIfF32Lt(condition, targetIp)
    is NumericCondition.F32Gt -> jumpIfF32Gt(condition, targetIp)
    is NumericCondition.F32Le -> jumpIfF32Le(condition, targetIp)
    is NumericCondition.F32Ge -> jumpIfF32Ge(condition, targetIp)
    is NumericCondition.F64Eq -> jumpIfF64Eq(condition, targetIp)
    is NumericCondition.F64Ne -> jumpIfF64Ne(condition, targetIp)
    is NumericCondition.F64Lt -> jumpIfF64Lt(condition, targetIp)
    is NumericCondition.F64Gt -> jumpIfF64Gt(condition, targetIp)
    is NumericCondition.F64Le -> jumpIfF64Le(condition, targetIp)
    is NumericCondition.F64Ge -> jumpIfF64Ge(condition, targetIp)
}

fun JumpConditionDispatcher(
    condition: NumericCondition,
    targetIp: Int,
    branchOnMatch: Boolean = true,
): DispatchableInstruction = if (branchOnMatch) {
    JumpIfConditionDispatcher(condition, targetIp)
} else {
    JumpConditionMismatchDispatcher(condition, targetIp)
}

private fun JumpConditionMismatchDispatcher(
    condition: NumericCondition,
    targetIp: Int,
): DispatchableInstruction = when (condition) {
    is NumericCondition.I32Eqz -> jumpUnlessI32Eqz(condition, targetIp)
    is NumericCondition.I32And -> jumpUnlessI32And(condition, targetIp)
    is NumericCondition.I64Eqz -> jumpUnlessI64Eqz(condition, targetIp)
    is NumericCondition.I32Eq -> jumpUnlessI32Eq(condition, targetIp)
    is NumericCondition.I32Ne -> jumpUnlessI32Ne(condition, targetIp)
    is NumericCondition.I32LtS -> jumpUnlessI32LtS(condition, targetIp)
    is NumericCondition.I32LtU -> jumpUnlessI32LtU(condition, targetIp)
    is NumericCondition.I32GtS -> jumpUnlessI32GtS(condition, targetIp)
    is NumericCondition.I32GtU -> jumpUnlessI32GtU(condition, targetIp)
    is NumericCondition.I32LeS -> jumpUnlessI32LeS(condition, targetIp)
    is NumericCondition.I32LeU -> jumpUnlessI32LeU(condition, targetIp)
    is NumericCondition.I32GeS -> jumpUnlessI32GeS(condition, targetIp)
    is NumericCondition.I32GeU -> jumpUnlessI32GeU(condition, targetIp)
    is NumericCondition.I64Eq -> jumpUnlessI64Eq(condition, targetIp)
    is NumericCondition.I64Ne -> jumpUnlessI64Ne(condition, targetIp)
    is NumericCondition.I64LtS -> jumpUnlessI64LtS(condition, targetIp)
    is NumericCondition.I64LtU -> jumpUnlessI64LtU(condition, targetIp)
    is NumericCondition.I64GtS -> jumpUnlessI64GtS(condition, targetIp)
    is NumericCondition.I64GtU -> jumpUnlessI64GtU(condition, targetIp)
    is NumericCondition.I64LeS -> jumpUnlessI64LeS(condition, targetIp)
    is NumericCondition.I64LeU -> jumpUnlessI64LeU(condition, targetIp)
    is NumericCondition.I64GeS -> jumpUnlessI64GeS(condition, targetIp)
    is NumericCondition.I64GeU -> jumpUnlessI64GeU(condition, targetIp)
    is NumericCondition.F32Eq -> jumpUnlessF32Eq(condition, targetIp)
    is NumericCondition.F32Ne -> jumpUnlessF32Ne(condition, targetIp)
    is NumericCondition.F32Lt -> jumpUnlessF32Lt(condition, targetIp)
    is NumericCondition.F32Gt -> jumpUnlessF32Gt(condition, targetIp)
    is NumericCondition.F32Le -> jumpUnlessF32Le(condition, targetIp)
    is NumericCondition.F32Ge -> jumpUnlessF32Ge(condition, targetIp)
    is NumericCondition.F64Eq -> jumpUnlessF64Eq(condition, targetIp)
    is NumericCondition.F64Ne -> jumpUnlessF64Ne(condition, targetIp)
    is NumericCondition.F64Lt -> jumpUnlessF64Lt(condition, targetIp)
    is NumericCondition.F64Gt -> jumpUnlessF64Gt(condition, targetIp)
    is NumericCondition.F64Le -> jumpUnlessF64Le(condition, targetIp)
    is NumericCondition.F64Ge -> jumpUnlessF64Ge(condition, targetIp)
}

private inline fun I32ConditionDispatcher(
    operand: FusedOperand,
    targetIp: Int,
    crossinline branch: (Int, Int, Int) -> Int,
): DispatchableInstruction = when (operand) {
    is FusedOperand.I32Const -> {
        val value = operand.const
        DispatchableInstruction { _, _, nextIp -> branch(value, targetIp, nextIp) }
    }
    is FusedOperand.FrameSlot -> {
        val slot = operand.offset
        DispatchableInstruction { vstack, _, nextIp ->
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
        DispatchableInstruction { _, _, nextIp -> branch(value, targetIp, nextIp) }
    }
    is FusedOperand.FrameSlot -> {
        val slot = operand.offset
        DispatchableInstruction { vstack, _, nextIp ->
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
            DispatchableInstruction { _, _, nextIp -> branch(leftValue, rightValue, targetIp, nextIp) }
        }
        is FusedOperand.FrameSlot -> {
            val leftValue = left.const
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, nextIp ->
                branch(leftValue, vstack.getFrameSlot(rightSlot).toInt(), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    is FusedOperand.FrameSlot -> when (right) {
        is FusedOperand.I32Const -> {
            val leftSlot = left.offset
            val rightValue = right.const
            DispatchableInstruction { vstack, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot).toInt(), rightValue, targetIp, nextIp)
            }
        }
        is FusedOperand.FrameSlot -> {
            val leftSlot = left.offset
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, nextIp ->
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
            DispatchableInstruction { _, _, nextIp -> branch(leftValue, rightValue, targetIp, nextIp) }
        }
        is FusedOperand.FrameSlot -> {
            val leftValue = left.const
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, nextIp ->
                branch(leftValue, vstack.getFrameSlot(rightSlot), targetIp, nextIp)
            }
        }
        else -> unsupportedConditionOperand(right)
    }
    is FusedOperand.FrameSlot -> when (right) {
        is FusedOperand.I64Const -> {
            val leftSlot = left.offset
            val rightValue = right.const
            DispatchableInstruction { vstack, _, nextIp ->
                branch(vstack.getFrameSlot(leftSlot), rightValue, targetIp, nextIp)
            }
        }
        is FusedOperand.FrameSlot -> {
            val leftSlot = left.offset
            val rightSlot = right.offset
            DispatchableInstruction { vstack, _, nextIp ->
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

internal fun FusedOperand.f32BitsOperand(): FusedOperand = when (this) {
    is FusedOperand.F32Const -> FusedOperand.I32Const(const.toRawBits())
    is FusedOperand.FrameSlot -> this
    else -> unsupportedConditionOperand(this)
}

internal fun FusedOperand.f64BitsOperand(): FusedOperand = when (this) {
    is FusedOperand.F64Const -> FusedOperand.I64Const(const.toRawBits())
    is FusedOperand.FrameSlot -> this
    else -> unsupportedConditionOperand(this)
}

fun JumpTableSDispatcher(
    operandSlot: Int,
    targetIps: IntArray,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, _ ->
        targetIps.branchTarget(vstack.getFrameSlot(operandSlot).toInt())
    }
}

internal inline fun IntArray.branchTarget(
    index: Int,
): Int {
    val defaultIndex = size - 1
    return if (index >= 0 && index < defaultIndex) this[index] else this[defaultIndex]
}

fun JumpOnNullIDispatcher(
    operand: Long,
    targetIp: Int,
): DispatchableInstruction = DispatchableInstruction { _, _, nextIp ->
    if (operand.isNullableReference()) targetIp else nextIp
}

fun JumpOnNullSDispatcher(
    operandSlot: Int,
    targetIp: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    if (vstack.getFrameSlot(operandSlot).isNullableReference()) targetIp else nextIp
}

fun JumpOnNonNullIDispatcher(
    operand: Long,
    targetIp: Int,
): DispatchableInstruction = DispatchableInstruction { _, _, nextIp ->
    if (!operand.isNullableReference()) targetIp else nextIp
}

fun JumpOnNonNullSDispatcher(
    operandSlot: Int,
    targetIp: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    if (!vstack.getFrameSlot(operandSlot).isNullableReference()) targetIp else nextIp
}

fun JumpOnCastIDispatcher(
    operand: Long,
    targetIp: Int,
    typeTest: ReferenceTypeTest,
): DispatchableInstruction = DispatchableInstruction { _, context, nextIp ->
    val matches = Caster(operand, typeTest, context)
    if (matches) targetIp else nextIp
}

fun JumpOnCastSDispatcher(
    operandSlot: Int,
    targetIp: Int,
    typeTest: ReferenceTypeTest,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    val operand = vstack.getFrameSlot(operandSlot)
    val matches = Caster(operand, typeTest, context)
    if (matches) targetIp else nextIp
}

fun JumpOnCastFailIDispatcher(
    operand: Long,
    targetIp: Int,
    typeTest: ReferenceTypeTest,
): DispatchableInstruction = DispatchableInstruction { _, context, nextIp ->
    val matches = Caster(operand, typeTest, context)
    if (!matches) targetIp else nextIp
}

fun JumpOnCastFailSDispatcher(
    operandSlot: Int,
    targetIp: Int,
    typeTest: ReferenceTypeTest,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    val operand = vstack.getFrameSlot(operandSlot)
    val matches = Caster(operand, typeTest, context)
    if (!matches) targetIp else nextIp
}

private fun jumpIfI32Eqz(
    condition: NumericCondition.I32Eqz,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.operand, target) { operand, targetIp, nextIp -> if (operand == 0) targetIp else nextIp }

private fun jumpIfI32And(
    condition: NumericCondition.I32And,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if ((left and right) != 0) targetIp else nextIp }

private fun jumpIfI64Eqz(
    condition: NumericCondition.I64Eqz,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.operand, target) { operand, targetIp, nextIp -> if (operand == 0L) targetIp else nextIp }

private fun jumpIfI32Eq(
    condition: NumericCondition.I32Eq,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }

private fun jumpIfI32Ne(
    condition: NumericCondition.I32Ne,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }

private fun jumpIfI32LtS(
    condition: NumericCondition.I32LtS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }

private fun jumpIfI32LtU(
    condition: NumericCondition.I32LtU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toUInt() < right.toUInt()) targetIp else nextIp }

private fun jumpIfI32GtS(
    condition: NumericCondition.I32GtS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }

private fun jumpIfI32GtU(
    condition: NumericCondition.I32GtU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toUInt() > right.toUInt()) targetIp else nextIp }

private fun jumpIfI32LeS(
    condition: NumericCondition.I32LeS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }

private fun jumpIfI32LeU(
    condition: NumericCondition.I32LeU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toUInt() <= right.toUInt()) targetIp else nextIp }

private fun jumpIfI32GeS(
    condition: NumericCondition.I32GeS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }

private fun jumpIfI32GeU(
    condition: NumericCondition.I32GeU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toUInt() >= right.toUInt()) targetIp else nextIp }

private fun jumpIfI64Eq(
    condition: NumericCondition.I64Eq,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }

private fun jumpIfI64Ne(
    condition: NumericCondition.I64Ne,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }

private fun jumpIfI64LtS(
    condition: NumericCondition.I64LtS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }

private fun jumpIfI64LtU(
    condition: NumericCondition.I64LtU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toULong() < right.toULong()) targetIp else nextIp }

private fun jumpIfI64GtS(
    condition: NumericCondition.I64GtS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }

private fun jumpIfI64GtU(
    condition: NumericCondition.I64GtU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toULong() > right.toULong()) targetIp else nextIp }

private fun jumpIfI64LeS(
    condition: NumericCondition.I64LeS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }

private fun jumpIfI64LeU(
    condition: NumericCondition.I64LeU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toULong() <= right.toULong()) targetIp else nextIp }

private fun jumpIfI64GeS(
    condition: NumericCondition.I64GeS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }

private fun jumpIfI64GeU(
    condition: NumericCondition.I64GeU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left.toULong() >= right.toULong()) targetIp else nextIp }

private fun jumpIfF32Eq(
    condition: NumericCondition.F32Eq,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }

private fun jumpIfF32Ne(
    condition: NumericCondition.F32Ne,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }

private fun jumpIfF32Lt(
    condition: NumericCondition.F32Lt,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }

private fun jumpIfF32Gt(
    condition: NumericCondition.F32Gt,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }

private fun jumpIfF32Le(
    condition: NumericCondition.F32Le,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }

private fun jumpIfF32Ge(
    condition: NumericCondition.F32Ge,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }

private fun jumpIfF64Eq(
    condition: NumericCondition.F64Eq,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left == right) targetIp else nextIp }

private fun jumpIfF64Ne(
    condition: NumericCondition.F64Ne,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left != right) targetIp else nextIp }

private fun jumpIfF64Lt(
    condition: NumericCondition.F64Lt,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left < right) targetIp else nextIp }

private fun jumpIfF64Gt(
    condition: NumericCondition.F64Gt,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left > right) targetIp else nextIp }

private fun jumpIfF64Le(
    condition: NumericCondition.F64Le,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left <= right) targetIp else nextIp }

private fun jumpIfF64Ge(
    condition: NumericCondition.F64Ge,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, targetIp, nextIp -> if (left >= right) targetIp else nextIp }

private fun jumpUnlessI32Eqz(
    condition: NumericCondition.I32Eqz,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.operand, target) { operand, branchIp, nextIp -> if (operand != 0) branchIp else nextIp }

private fun jumpUnlessI32And(
    condition: NumericCondition.I32And,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if ((left and right) == 0) branchIp else nextIp }

private fun jumpUnlessI64Eqz(
    condition: NumericCondition.I64Eqz,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.operand, target) { operand, branchIp, nextIp -> if (operand != 0L) branchIp else nextIp }

private fun jumpUnlessI32Eq(
    condition: NumericCondition.I32Eq,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }

private fun jumpUnlessI32Ne(
    condition: NumericCondition.I32Ne,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }

private fun jumpUnlessI32LtS(
    condition: NumericCondition.I32LtS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left >= right) branchIp else nextIp }

private fun jumpUnlessI32LtU(
    condition: NumericCondition.I32LtU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toUInt() >= right.toUInt()) branchIp else nextIp }

private fun jumpUnlessI32GtS(
    condition: NumericCondition.I32GtS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left <= right) branchIp else nextIp }

private fun jumpUnlessI32GtU(
    condition: NumericCondition.I32GtU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toUInt() <= right.toUInt()) branchIp else nextIp }

private fun jumpUnlessI32LeS(
    condition: NumericCondition.I32LeS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left > right) branchIp else nextIp }

private fun jumpUnlessI32LeU(
    condition: NumericCondition.I32LeU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toUInt() > right.toUInt()) branchIp else nextIp }

private fun jumpUnlessI32GeS(
    condition: NumericCondition.I32GeS,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left < right) branchIp else nextIp }

private fun jumpUnlessI32GeU(
    condition: NumericCondition.I32GeU,
    target: Int,
): DispatchableInstruction = I32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toUInt() < right.toUInt()) branchIp else nextIp }

private fun jumpUnlessI64Eq(
    condition: NumericCondition.I64Eq,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }

private fun jumpUnlessI64Ne(
    condition: NumericCondition.I64Ne,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }

private fun jumpUnlessI64LtS(
    condition: NumericCondition.I64LtS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left >= right) branchIp else nextIp }

private fun jumpUnlessI64LtU(
    condition: NumericCondition.I64LtU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toULong() >= right.toULong()) branchIp else nextIp }

private fun jumpUnlessI64GtS(
    condition: NumericCondition.I64GtS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left <= right) branchIp else nextIp }

private fun jumpUnlessI64GtU(
    condition: NumericCondition.I64GtU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toULong() <= right.toULong()) branchIp else nextIp }

private fun jumpUnlessI64LeS(
    condition: NumericCondition.I64LeS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left > right) branchIp else nextIp }

private fun jumpUnlessI64LeU(
    condition: NumericCondition.I64LeU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toULong() > right.toULong()) branchIp else nextIp }

private fun jumpUnlessI64GeS(
    condition: NumericCondition.I64GeS,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left < right) branchIp else nextIp }

private fun jumpUnlessI64GeU(
    condition: NumericCondition.I64GeU,
    target: Int,
): DispatchableInstruction = I64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left.toULong() < right.toULong()) branchIp else nextIp }

private fun jumpUnlessF32Eq(
    condition: NumericCondition.F32Eq,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }

private fun jumpUnlessF32Ne(
    condition: NumericCondition.F32Ne,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }

private fun jumpUnlessF32Lt(
    condition: NumericCondition.F32Lt,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left < right)) branchIp else nextIp }

private fun jumpUnlessF32Gt(
    condition: NumericCondition.F32Gt,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left > right)) branchIp else nextIp }

private fun jumpUnlessF32Le(
    condition: NumericCondition.F32Le,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left <= right)) branchIp else nextIp }

private fun jumpUnlessF32Ge(
    condition: NumericCondition.F32Ge,
    target: Int,
): DispatchableInstruction = F32ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left >= right)) branchIp else nextIp }

private fun jumpUnlessF64Eq(
    condition: NumericCondition.F64Eq,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left != right) branchIp else nextIp }

private fun jumpUnlessF64Ne(
    condition: NumericCondition.F64Ne,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (left == right) branchIp else nextIp }

private fun jumpUnlessF64Lt(
    condition: NumericCondition.F64Lt,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left < right)) branchIp else nextIp }

private fun jumpUnlessF64Gt(
    condition: NumericCondition.F64Gt,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left > right)) branchIp else nextIp }

private fun jumpUnlessF64Le(
    condition: NumericCondition.F64Le,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left <= right)) branchIp else nextIp }

private fun jumpUnlessF64Ge(
    condition: NumericCondition.F64Ge,
    target: Int,
): DispatchableInstruction = F64ConditionDispatcher(condition.left, condition.right, target) { left, right, branchIp, nextIp -> if (!(left >= right)) branchIp else nextIp }
