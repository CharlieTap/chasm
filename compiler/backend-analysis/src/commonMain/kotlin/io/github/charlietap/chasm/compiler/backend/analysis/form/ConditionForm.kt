package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.type.NumberType

sealed interface ConditionForm {

    class Compare(
        val type: NumberType,
        val comparison: Comparison,
        val left: Operand,
        val right: Operand,
    ) : ConditionForm

    class And(
        val left: Operand,
        val right: Operand,
    ) : ConditionForm
}

val ConditionForm.negatable: Boolean
    get() = this !is ConditionForm.Compare || type == NumberType.I32 || type == NumberType.I64

fun NumericCondition.form(): ConditionForm = when (val c = this) {
    is NumericCondition.I32Eqz -> compare(NumberType.I32, Comparison.Eq, c.operand.operand(), ZERO)
    is NumericCondition.I64Eqz -> compare(NumberType.I64, Comparison.Eq, c.operand.operand(), ZERO)
    is NumericCondition.I32And -> ConditionForm.And(c.left.operand(), c.right.operand())
    is NumericCondition.I32Eq -> compare(NumberType.I32, Comparison.Eq, c.left.operand(), c.right.operand())
    is NumericCondition.I32Ne -> compare(NumberType.I32, Comparison.Ne, c.left.operand(), c.right.operand())
    is NumericCondition.I32LtS -> compare(NumberType.I32, Comparison.LtS, c.left.operand(), c.right.operand())
    is NumericCondition.I32LtU -> compare(NumberType.I32, Comparison.LtU, c.left.operand(), c.right.operand())
    is NumericCondition.I32GtS -> compare(NumberType.I32, Comparison.GtS, c.left.operand(), c.right.operand())
    is NumericCondition.I32GtU -> compare(NumberType.I32, Comparison.GtU, c.left.operand(), c.right.operand())
    is NumericCondition.I32LeS -> compare(NumberType.I32, Comparison.LeS, c.left.operand(), c.right.operand())
    is NumericCondition.I32LeU -> compare(NumberType.I32, Comparison.LeU, c.left.operand(), c.right.operand())
    is NumericCondition.I32GeS -> compare(NumberType.I32, Comparison.GeS, c.left.operand(), c.right.operand())
    is NumericCondition.I32GeU -> compare(NumberType.I32, Comparison.GeU, c.left.operand(), c.right.operand())
    is NumericCondition.I64Eq -> compare(NumberType.I64, Comparison.Eq, c.left.operand(), c.right.operand())
    is NumericCondition.I64Ne -> compare(NumberType.I64, Comparison.Ne, c.left.operand(), c.right.operand())
    is NumericCondition.I64LtS -> compare(NumberType.I64, Comparison.LtS, c.left.operand(), c.right.operand())
    is NumericCondition.I64LtU -> compare(NumberType.I64, Comparison.LtU, c.left.operand(), c.right.operand())
    is NumericCondition.I64GtS -> compare(NumberType.I64, Comparison.GtS, c.left.operand(), c.right.operand())
    is NumericCondition.I64GtU -> compare(NumberType.I64, Comparison.GtU, c.left.operand(), c.right.operand())
    is NumericCondition.I64LeS -> compare(NumberType.I64, Comparison.LeS, c.left.operand(), c.right.operand())
    is NumericCondition.I64LeU -> compare(NumberType.I64, Comparison.LeU, c.left.operand(), c.right.operand())
    is NumericCondition.I64GeS -> compare(NumberType.I64, Comparison.GeS, c.left.operand(), c.right.operand())
    is NumericCondition.I64GeU -> compare(NumberType.I64, Comparison.GeU, c.left.operand(), c.right.operand())
    is NumericCondition.F32Eq -> compare(NumberType.F32, Comparison.Eq, c.left.operand(), c.right.operand())
    is NumericCondition.F32Ne -> compare(NumberType.F32, Comparison.Ne, c.left.operand(), c.right.operand())
    is NumericCondition.F32Lt -> compare(NumberType.F32, Comparison.Lt, c.left.operand(), c.right.operand())
    is NumericCondition.F32Gt -> compare(NumberType.F32, Comparison.Gt, c.left.operand(), c.right.operand())
    is NumericCondition.F32Le -> compare(NumberType.F32, Comparison.Le, c.left.operand(), c.right.operand())
    is NumericCondition.F32Ge -> compare(NumberType.F32, Comparison.Ge, c.left.operand(), c.right.operand())
    is NumericCondition.F64Eq -> compare(NumberType.F64, Comparison.Eq, c.left.operand(), c.right.operand())
    is NumericCondition.F64Ne -> compare(NumberType.F64, Comparison.Ne, c.left.operand(), c.right.operand())
    is NumericCondition.F64Lt -> compare(NumberType.F64, Comparison.Lt, c.left.operand(), c.right.operand())
    is NumericCondition.F64Gt -> compare(NumberType.F64, Comparison.Gt, c.left.operand(), c.right.operand())
    is NumericCondition.F64Le -> compare(NumberType.F64, Comparison.Le, c.left.operand(), c.right.operand())
    is NumericCondition.F64Ge -> compare(NumberType.F64, Comparison.Ge, c.left.operand(), c.right.operand())
}

private val ZERO: Operand = Operand.Immediate(0)

private fun compare(
    type: NumberType,
    comparison: Comparison,
    left: Operand,
    right: Operand,
) =
    ConditionForm.Compare(type, comparison, left, right)
