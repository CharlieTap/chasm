package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.f32Immediate
import io.github.charlietap.chasm.compiler.operand.f64Immediate
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.NumericCondition

internal val NumericOpcode.isCondition: Boolean
    get() = when (this) {
        NumericOpcode.I32Eqz,
        NumericOpcode.I64Eqz,
        NumericOpcode.I32Eq,
        NumericOpcode.I32Ne,
        NumericOpcode.I32LtS,
        NumericOpcode.I32LtU,
        NumericOpcode.I32GtS,
        NumericOpcode.I32GtU,
        NumericOpcode.I32LeS,
        NumericOpcode.I32LeU,
        NumericOpcode.I32GeS,
        NumericOpcode.I32GeU,
        NumericOpcode.I64Eq,
        NumericOpcode.I64Ne,
        NumericOpcode.I64LtS,
        NumericOpcode.I64LtU,
        NumericOpcode.I64GtS,
        NumericOpcode.I64GtU,
        NumericOpcode.I64LeS,
        NumericOpcode.I64LeU,
        NumericOpcode.I64GeS,
        NumericOpcode.I64GeU,
        NumericOpcode.F32Eq,
        NumericOpcode.F32Ne,
        NumericOpcode.F32Lt,
        NumericOpcode.F32Gt,
        NumericOpcode.F32Le,
        NumericOpcode.F32Ge,
        NumericOpcode.F64Eq,
        NumericOpcode.F64Ne,
        NumericOpcode.F64Lt,
        NumericOpcode.F64Gt,
        NumericOpcode.F64Le,
        NumericOpcode.F64Ge,
        -> true
        else -> false
    }

internal fun FunctionCompilationContext.popNumericCondition(
    instruction: NumericInstruction.Operator,
): NumericCondition {
    val opcode = instruction.opcode
    check(opcode.isCondition)
    val right = pop()
    val rightSource = right.toFusedOperand()
    if (opcode == NumericOpcode.I32Eqz) return NumericCondition.I32Eqz(rightSource)
    if (opcode == NumericOpcode.I64Eqz) return NumericCondition.I64Eqz(rightSource)

    val leftSource = pop().toFusedOperand()
    return when (opcode) {
        NumericOpcode.I32Eq -> NumericCondition.I32Eq(leftSource, rightSource)
        NumericOpcode.I32Ne -> NumericCondition.I32Ne(leftSource, rightSource)
        NumericOpcode.I32LtS -> NumericCondition.I32LtS(leftSource, rightSource)
        NumericOpcode.I32LtU -> NumericCondition.I32LtU(leftSource, rightSource)
        NumericOpcode.I32GtS -> NumericCondition.I32GtS(leftSource, rightSource)
        NumericOpcode.I32GtU -> NumericCondition.I32GtU(leftSource, rightSource)
        NumericOpcode.I32LeS -> NumericCondition.I32LeS(leftSource, rightSource)
        NumericOpcode.I32LeU -> NumericCondition.I32LeU(leftSource, rightSource)
        NumericOpcode.I32GeS -> NumericCondition.I32GeS(leftSource, rightSource)
        NumericOpcode.I32GeU -> NumericCondition.I32GeU(leftSource, rightSource)
        NumericOpcode.I64Eq -> NumericCondition.I64Eq(leftSource, rightSource)
        NumericOpcode.I64Ne -> NumericCondition.I64Ne(leftSource, rightSource)
        NumericOpcode.I64LtS -> NumericCondition.I64LtS(leftSource, rightSource)
        NumericOpcode.I64LtU -> NumericCondition.I64LtU(leftSource, rightSource)
        NumericOpcode.I64GtS -> NumericCondition.I64GtS(leftSource, rightSource)
        NumericOpcode.I64GtU -> NumericCondition.I64GtU(leftSource, rightSource)
        NumericOpcode.I64LeS -> NumericCondition.I64LeS(leftSource, rightSource)
        NumericOpcode.I64LeU -> NumericCondition.I64LeU(leftSource, rightSource)
        NumericOpcode.I64GeS -> NumericCondition.I64GeS(leftSource, rightSource)
        NumericOpcode.I64GeU -> NumericCondition.I64GeU(leftSource, rightSource)
        NumericOpcode.F32Eq -> NumericCondition.F32Eq(leftSource, rightSource)
        NumericOpcode.F32Ne -> NumericCondition.F32Ne(leftSource, rightSource)
        NumericOpcode.F32Lt -> NumericCondition.F32Lt(leftSource, rightSource)
        NumericOpcode.F32Gt -> NumericCondition.F32Gt(leftSource, rightSource)
        NumericOpcode.F32Le -> NumericCondition.F32Le(leftSource, rightSource)
        NumericOpcode.F32Ge -> NumericCondition.F32Ge(leftSource, rightSource)
        NumericOpcode.F64Eq -> NumericCondition.F64Eq(leftSource, rightSource)
        NumericOpcode.F64Ne -> NumericCondition.F64Ne(leftSource, rightSource)
        NumericOpcode.F64Lt -> NumericCondition.F64Lt(leftSource, rightSource)
        NumericOpcode.F64Gt -> NumericCondition.F64Gt(leftSource, rightSource)
        NumericOpcode.F64Le -> NumericCondition.F64Le(leftSource, rightSource)
        NumericOpcode.F64Ge -> NumericCondition.F64Ge(leftSource, rightSource)
        else -> error("not a binary numeric condition: $opcode")
    }
}

private fun OperandSource.toFusedOperand(): FusedOperand = when (sourceKind) {
    OperandSourceKind.I32Immediate -> FusedOperand.I32Const(i32Immediate)
    OperandSourceKind.I64Immediate -> FusedOperand.I64Const(i64Immediate)
    OperandSourceKind.F32Immediate -> FusedOperand.F32Const(f32Immediate)
    OperandSourceKind.F64Immediate -> FusedOperand.F64Const(f64Immediate)
    OperandSourceKind.Local,
    OperandSourceKind.Frame,
    -> FusedOperand.FrameSlot(sourceSlot)
}
