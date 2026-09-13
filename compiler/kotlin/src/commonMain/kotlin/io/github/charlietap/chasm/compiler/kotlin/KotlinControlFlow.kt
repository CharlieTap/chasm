package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.TransferSource

internal data class KotlinCopies(val sources: List<KotlinValueInput>, val destinations: List<Int>, val sequential: Boolean = false)

internal data class KotlinBranch(
    val targets: List<Int>,
    val condition: KotlinValueInstruction? = null,
    val branchOnMatch: Boolean = true,
    val copies: KotlinCopies? = null,
    val table: Boolean = false,
)

internal fun copies(instruction: LinkedInstruction): KotlinCopies? = when (instruction) {
    is AdminInstruction.CopySlots -> KotlinCopies(instruction.sourceSlots.map { KotlinValueInput.Slot(it, KotlinValueType.I64) }, instruction.destinationSlots.toList(), instruction.sequential)
    else -> null
}

internal fun branch(instruction: LinkedInstruction): KotlinBranch? {
    fun predicate(input: KotlinValueInput) = KotlinValueInstruction(null, listOf(input), "@0@", KotlinValueType.I64)

    fun slot(value: Int) = predicate(KotlinValueInput.Slot(value, KotlinValueType.I64))

    fun immediate(value: Long) = predicate(KotlinValueInput.Literal(longLiteral(value), KotlinValueType.I64))

    fun copy(source: Int, destination: Int) = KotlinCopies(listOf(KotlinValueInput.Slot(source, KotlinValueType.I64)), listOf(destination))
    return when (instruction) {
        is AdminInstruction.Jump -> KotlinBranch(listOf(instruction.targetIp))
        is AdminInstruction.JumpCopies -> KotlinBranch(
            listOf(instruction.targetIp),
            copies = KotlinCopies(
                instruction.operands.sources.map { source ->
                    when (source) {
                        is TransferSource.Slot -> KotlinValueInput.Slot(source.slot, KotlinValueType.I64)
                        is TransferSource.Immediate -> KotlinValueInput.Literal(longLiteral(source.value), KotlinValueType.I64)
                    }
                },
                instruction.operands.sources.indices.map { instruction.destinationSlotBase + it },
            ),
        )
        is AdminInstruction.JumpIfI -> KotlinBranch(listOf(instruction.targetIp), immediate(instruction.operand))
        is AdminInstruction.JumpIfS -> KotlinBranch(listOf(instruction.targetIp), slot(instruction.operandSlot))
        is AdminInstruction.JumpIfZeroI -> KotlinBranch(listOf(instruction.targetIp), immediate(instruction.operand), false)
        is AdminInstruction.JumpIfZeroS -> KotlinBranch(listOf(instruction.targetIp), slot(instruction.operandSlot), false)
        is AdminInstruction.JumpIfCopyI -> KotlinBranch(listOf(instruction.targetIp), immediate(instruction.operand), copies = copy(instruction.sourceSlot, instruction.destinationSlot))
        is AdminInstruction.JumpIfCopyS -> KotlinBranch(listOf(instruction.targetIp), slot(instruction.operandSlot), copies = copy(instruction.sourceSlot, instruction.destinationSlot))
        is AdminInstruction.JumpIfCondition -> KotlinBranch(listOf(instruction.targetIp), numericPredicate(instruction.condition))
        is AdminInstruction.JumpIfConditionMismatch -> KotlinBranch(listOf(instruction.targetIp), numericPredicate(instruction.condition), false)
        is AdminInstruction.JumpTableS -> KotlinBranch(instruction.targetIps.toList(), slot(instruction.operandSlot), table = true)
        else -> null
    }
}

internal fun longLiteral(value: Long): String = if (value == Long.MIN_VALUE) "Long.MIN_VALUE" else "${value}L"

private fun fusedInput(operand: FusedOperand, type: KotlinValueType): KotlinValueInput = when (operand) {
    is FusedOperand.FrameSlot -> KotlinValueInput.Slot(operand.offset, type)
    is FusedOperand.I32Const -> KotlinValueInput.Literal(operand.const.toString(), type)
    is FusedOperand.I64Const -> KotlinValueInput.Literal(longLiteral(operand.const), type)
    is FusedOperand.F32Const -> KotlinValueInput.Literal("Float.fromBits(${operand.const.toRawBits()})", type)
    is FusedOperand.F64Const -> KotlinValueInput.Literal("Double.fromBits(${longLiteral(operand.const.toRawBits())})", type)
    else -> error("Numeric condition must use lowered frame operands: $operand")
}

private fun numericPredicate(condition: NumericCondition): KotlinValueInstruction {
    fun scalar(type: KotlinValueType, family: String, name: String, vararg operands: FusedOperand): KotlinValueInstruction {
        val inputs = operands.map { fusedInput(it, type) }
        val arguments = operands.indices.joinToString { "@$it@" }
        val call = "io.github.charlietap.chasm.executor.invoker.instruction.numeric.$family.value$name($arguments)"
        return KotlinValueInstruction(null, inputs, "$call != ${if (type == KotlinValueType.I64) "0L" else "0"}", KotlinValueType.BOOL)
    }

    fun floating(type: KotlinValueType, name: String, left: FusedOperand, right: FusedOperand) = KotlinValueInstruction(
        null,
        listOf(fusedInput(left, type), fusedInput(right, type)),
        "(@0@).$name(@1@)",
        KotlinValueType.BOOL,
    )
    return when (condition) {
        is NumericCondition.I32Eqz -> scalar(KotlinValueType.I32, "testop", "I32Eqz", condition.operand)
        is NumericCondition.I32And -> scalar(KotlinValueType.I32, "binop", "I32And", condition.left, condition.right)
        is NumericCondition.I64Eqz -> scalar(KotlinValueType.I64, "testop", "I64Eqz", condition.operand)
        is NumericCondition.I32Eq -> scalar(KotlinValueType.I32, "relop", "I32Eq", condition.left, condition.right)
        is NumericCondition.I32Ne -> scalar(KotlinValueType.I32, "relop", "I32Ne", condition.left, condition.right)
        is NumericCondition.I32LtS -> scalar(KotlinValueType.I32, "relop", "I32LtS", condition.left, condition.right)
        is NumericCondition.I32LtU -> scalar(KotlinValueType.I32, "relop", "I32LtU", condition.left, condition.right)
        is NumericCondition.I32GtS -> scalar(KotlinValueType.I32, "relop", "I32GtS", condition.left, condition.right)
        is NumericCondition.I32GtU -> scalar(KotlinValueType.I32, "relop", "I32GtU", condition.left, condition.right)
        is NumericCondition.I32LeS -> scalar(KotlinValueType.I32, "relop", "I32LeS", condition.left, condition.right)
        is NumericCondition.I32LeU -> scalar(KotlinValueType.I32, "relop", "I32LeU", condition.left, condition.right)
        is NumericCondition.I32GeS -> scalar(KotlinValueType.I32, "relop", "I32GeS", condition.left, condition.right)
        is NumericCondition.I32GeU -> scalar(KotlinValueType.I32, "relop", "I32GeU", condition.left, condition.right)
        is NumericCondition.I64Eq -> scalar(KotlinValueType.I64, "relop", "I64Eq", condition.left, condition.right)
        is NumericCondition.I64Ne -> scalar(KotlinValueType.I64, "relop", "I64Ne", condition.left, condition.right)
        is NumericCondition.I64LtS -> scalar(KotlinValueType.I64, "relop", "I64LtS", condition.left, condition.right)
        is NumericCondition.I64LtU -> scalar(KotlinValueType.I64, "relop", "I64LtU", condition.left, condition.right)
        is NumericCondition.I64GtS -> scalar(KotlinValueType.I64, "relop", "I64GtS", condition.left, condition.right)
        is NumericCondition.I64GtU -> scalar(KotlinValueType.I64, "relop", "I64GtU", condition.left, condition.right)
        is NumericCondition.I64LeS -> scalar(KotlinValueType.I64, "relop", "I64LeS", condition.left, condition.right)
        is NumericCondition.I64LeU -> scalar(KotlinValueType.I64, "relop", "I64LeU", condition.left, condition.right)
        is NumericCondition.I64GeS -> scalar(KotlinValueType.I64, "relop", "I64GeS", condition.left, condition.right)
        is NumericCondition.I64GeU -> scalar(KotlinValueType.I64, "relop", "I64GeU", condition.left, condition.right)
        is NumericCondition.F32Eq -> floating(KotlinValueType.F32, "eq", condition.left, condition.right)
        is NumericCondition.F32Ne -> floating(KotlinValueType.F32, "ne", condition.left, condition.right)
        is NumericCondition.F32Lt -> floating(KotlinValueType.F32, "lt", condition.left, condition.right)
        is NumericCondition.F32Gt -> floating(KotlinValueType.F32, "gt", condition.left, condition.right)
        is NumericCondition.F32Le -> floating(KotlinValueType.F32, "le", condition.left, condition.right)
        is NumericCondition.F32Ge -> floating(KotlinValueType.F32, "ge", condition.left, condition.right)
        is NumericCondition.F64Eq -> floating(KotlinValueType.F64, "eq", condition.left, condition.right)
        is NumericCondition.F64Ne -> floating(KotlinValueType.F64, "ne", condition.left, condition.right)
        is NumericCondition.F64Lt -> floating(KotlinValueType.F64, "lt", condition.left, condition.right)
        is NumericCondition.F64Gt -> floating(KotlinValueType.F64, "gt", condition.left, condition.right)
        is NumericCondition.F64Le -> floating(KotlinValueType.F64, "le", condition.left, condition.right)
        is NumericCondition.F64Ge -> floating(KotlinValueType.F64, "ge", condition.left, condition.right)
    }
}
