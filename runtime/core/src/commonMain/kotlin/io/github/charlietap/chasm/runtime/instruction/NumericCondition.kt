package io.github.charlietap.chasm.runtime.instruction

sealed interface NumericCondition {

    data class I32Eqz(val operand: FusedOperand) : NumericCondition

    data class I64Eqz(val operand: FusedOperand) : NumericCondition

    data class I32Eq(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32Ne(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32LtS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32LtU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32GtS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32GtU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32LeS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32LeU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32GeS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I32GeU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64Eq(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64Ne(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64LtS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64LtU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64GtS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64GtU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64LeS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64LeU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64GeS(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class I64GeU(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Eq(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Ne(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Lt(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Gt(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Le(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F32Ge(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Eq(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Ne(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Lt(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Gt(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Le(val left: FusedOperand, val right: FusedOperand) : NumericCondition

    data class F64Ge(val left: FusedOperand, val right: FusedOperand) : NumericCondition
}
