package io.github.charlietap.chasm.executor.runtime.instruction

sealed interface FusedNumericInstruction : LinkedInstruction {

    data class I32Add(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Sub(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Mul(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32DivS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32DivU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32RemS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32RemU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32And(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Or(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Xor(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Shl(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32ShrS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32ShrU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Rotl(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Rotr(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Add(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Sub(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Mul(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64DivS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64DivU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64RemS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64RemU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64And(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Or(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Xor(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Shl(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64ShrS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64ShrU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Rotl(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Rotr(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Add(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Sub(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Mul(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Div(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Min(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Max(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Copysign(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Add(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Sub(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Mul(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Div(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Min(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Max(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Copysign(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Eqz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Eqz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Abs(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Neg(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Ceil(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Floor(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Trunc(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Nearest(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Sqrt(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Abs(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Neg(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Ceil(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Floor(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Trunc(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Nearest(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Sqrt(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Clz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Ctz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Popcnt(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Clz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Ctz(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Popcnt(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Eq(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Ne(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32LtS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32LtU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32GtS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32GtU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32LeS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32LeU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32GeS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32GeU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Eq(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Ne(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64LtS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64LtU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64GtS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64GtU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64LeS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64LeU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64GeS(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64GeU(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Eq(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Ne(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Lt(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Gt(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Le(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Ge(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Eq(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Ne(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Lt(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Gt(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Le(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64Ge(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32ConvertI32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32ConvertI32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32ConvertI64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32ConvertI64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32DemoteF64(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32ReinterpretI32(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64ConvertI32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64ConvertI32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64ConvertI64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64ConvertI64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64PromoteF32(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F64ReinterpretI64(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Extend16S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Extend8S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32ReinterpretF32(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncF32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncF32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncF64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncF64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncSatF32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncSatF32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncSatF64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32TruncSatF64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32WrapI64(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Extend16S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Extend32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64Extend8S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64ExtendI32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64ExtendI32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64ReinterpretF64(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncF32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncF32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncF64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncF64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncSatF32S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncSatF32U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncSatF64S(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I64TruncSatF64U(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction
}
