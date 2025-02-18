package io.github.charlietap.chasm.ir.instruction

sealed interface FusedNumericInstruction : Instruction {

    data class I32Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Sub(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Mul(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32DivS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32DivU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32RemS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32RemU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32And(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Or(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Xor(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Shl(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32ShrS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32ShrU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Rotl(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Rotr(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Sub(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Mul(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64DivS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64DivU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64RemS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64RemU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64And(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Or(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Xor(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Shl(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64ShrS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64ShrU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Rotl(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Rotr(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Sub(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Mul(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Div(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Min(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Max(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Copysign(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Sub(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Mul(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Div(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Min(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Max(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Copysign(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Eqz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Eqz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Abs(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Neg(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Ceil(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Floor(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Trunc(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Nearest(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Sqrt(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Abs(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Neg(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Ceil(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Floor(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Trunc(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Nearest(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Sqrt(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Clz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Ctz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Popcnt(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Clz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Ctz(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Popcnt(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Eq(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Ne(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32LtS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32LtU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32GtS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32GtU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32LeS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32LeU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32GeS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32GeU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Eq(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Ne(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64LtS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64LtU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64GtS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64GtU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64LeS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64LeU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64GeS(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64GeU(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Eq(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Ne(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Lt(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Gt(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Le(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Ge(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Eq(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Ne(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Lt(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Gt(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Le(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64Ge(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32ConvertI32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32ConvertI32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32ConvertI64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32ConvertI64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32DemoteF64(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32ReinterpretI32(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64ConvertI32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64ConvertI32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64ConvertI64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64ConvertI64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64PromoteF32(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F64ReinterpretI64(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Extend16S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Extend8S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32ReinterpretF32(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncF32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncF32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncF64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncF64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncSatF32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncSatF32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncSatF64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32TruncSatF64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32WrapI64(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Extend16S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Extend32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64Extend8S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64ExtendI32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64ExtendI32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64ReinterpretF64(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncF32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncF32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncF64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncF64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncSatF32S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncSatF32U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncSatF64S(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I64TruncSatF64U(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction
}
