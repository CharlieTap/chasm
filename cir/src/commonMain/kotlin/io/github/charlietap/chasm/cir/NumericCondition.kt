package io.github.charlietap.chasm.cir

sealed interface NumericCondition {

    @CirTag("i32.eqz")
    data class I32Eqz(
        val operand: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.and")
    data class I32And(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.eqz")
    data class I64Eqz(
        val operand: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.eq")
    data class I32Eq(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.ne")
    data class I32Ne(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.lt_s")
    data class I32LtS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.lt_u")
    data class I32LtU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.gt_s")
    data class I32GtS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.gt_u")
    data class I32GtU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.le_s")
    data class I32LeS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.le_u")
    data class I32LeU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.ge_s")
    data class I32GeS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i32.ge_u")
    data class I32GeU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.eq")
    data class I64Eq(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.ne")
    data class I64Ne(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.lt_s")
    data class I64LtS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.lt_u")
    data class I64LtU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.gt_s")
    data class I64GtS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.gt_u")
    data class I64GtU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.le_s")
    data class I64LeS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.le_u")
    data class I64LeU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.ge_s")
    data class I64GeS(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("i64.ge_u")
    data class I64GeU(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.eq")
    data class F32Eq(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.ne")
    data class F32Ne(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.lt")
    data class F32Lt(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.gt")
    data class F32Gt(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.le")
    data class F32Le(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f32.ge")
    data class F32Ge(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.eq")
    data class F64Eq(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.ne")
    data class F64Ne(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.lt")
    data class F64Lt(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.gt")
    data class F64Gt(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.le")
    data class F64Le(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition

    @CirTag("f64.ge")
    data class F64Ge(
        val left: FusedOperand,
        val right: FusedOperand,
    ) : NumericCondition
}
