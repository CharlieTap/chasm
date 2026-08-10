package io.github.charlietap.chasm.ast.instruction

import kotlin.jvm.JvmInline

sealed interface NumericInstruction : Instruction {

    sealed class Operator(
        val opcode: NumericOpcode,
    ) : NumericInstruction

    @JvmInline
    value class I32Const(val value: Int) : NumericInstruction

    @JvmInline
    value class I64Const(val value: Long) : NumericInstruction

    data class F32Const(val value: Float, val bits: Int) : NumericInstruction

    data class F64Const(val value: Double, val bits: Long) : NumericInstruction

    data object I32Eqz : Operator(NumericOpcode.I32Eqz)

    data object I32Eq : Operator(NumericOpcode.I32Eq)

    data object I32Ne : Operator(NumericOpcode.I32Ne)

    data object I32LtS : Operator(NumericOpcode.I32LtS)

    data object I32LtU : Operator(NumericOpcode.I32LtU)

    data object I32GtS : Operator(NumericOpcode.I32GtS)

    data object I32GtU : Operator(NumericOpcode.I32GtU)

    data object I32LeS : Operator(NumericOpcode.I32LeS)

    data object I32LeU : Operator(NumericOpcode.I32LeU)

    data object I32GeS : Operator(NumericOpcode.I32GeS)

    data object I32GeU : Operator(NumericOpcode.I32GeU)

    data object I64Eqz : Operator(NumericOpcode.I64Eqz)

    data object I64Eq : Operator(NumericOpcode.I64Eq)

    data object I64Ne : Operator(NumericOpcode.I64Ne)

    data object I64LtS : Operator(NumericOpcode.I64LtS)

    data object I64LtU : Operator(NumericOpcode.I64LtU)

    data object I64GtS : Operator(NumericOpcode.I64GtS)

    data object I64GtU : Operator(NumericOpcode.I64GtU)

    data object I64LeS : Operator(NumericOpcode.I64LeS)

    data object I64LeU : Operator(NumericOpcode.I64LeU)

    data object I64GeS : Operator(NumericOpcode.I64GeS)

    data object I64GeU : Operator(NumericOpcode.I64GeU)

    data object F32Eq : Operator(NumericOpcode.F32Eq)

    data object F32Ne : Operator(NumericOpcode.F32Ne)

    data object F32Lt : Operator(NumericOpcode.F32Lt)

    data object F32Gt : Operator(NumericOpcode.F32Gt)

    data object F32Le : Operator(NumericOpcode.F32Le)

    data object F32Ge : Operator(NumericOpcode.F32Ge)

    data object F64Eq : Operator(NumericOpcode.F64Eq)

    data object F64Ne : Operator(NumericOpcode.F64Ne)

    data object F64Lt : Operator(NumericOpcode.F64Lt)

    data object F64Gt : Operator(NumericOpcode.F64Gt)

    data object F64Le : Operator(NumericOpcode.F64Le)

    data object F64Ge : Operator(NumericOpcode.F64Ge)

    data object I32Clz : Operator(NumericOpcode.I32Clz)

    data object I32Ctz : Operator(NumericOpcode.I32Ctz)

    data object I32Popcnt : Operator(NumericOpcode.I32Popcnt)

    data object I32Add : Operator(NumericOpcode.I32Add)

    data object I32Sub : Operator(NumericOpcode.I32Sub)

    data object I32Mul : Operator(NumericOpcode.I32Mul)

    data object I32DivS : Operator(NumericOpcode.I32DivS)

    data object I32DivU : Operator(NumericOpcode.I32DivU)

    data object I32RemS : Operator(NumericOpcode.I32RemS)

    data object I32RemU : Operator(NumericOpcode.I32RemU)

    data object I32And : Operator(NumericOpcode.I32And)

    data object I32Or : Operator(NumericOpcode.I32Or)

    data object I32Xor : Operator(NumericOpcode.I32Xor)

    data object I32Shl : Operator(NumericOpcode.I32Shl)

    data object I32ShrS : Operator(NumericOpcode.I32ShrS)

    data object I32ShrU : Operator(NumericOpcode.I32ShrU)

    data object I32Rotl : Operator(NumericOpcode.I32Rotl)

    data object I32Rotr : Operator(NumericOpcode.I32Rotr)

    data object I64Clz : Operator(NumericOpcode.I64Clz)

    data object I64Ctz : Operator(NumericOpcode.I64Ctz)

    data object I64Popcnt : Operator(NumericOpcode.I64Popcnt)

    data object I64Add : Operator(NumericOpcode.I64Add)

    data object I64Sub : Operator(NumericOpcode.I64Sub)

    data object I64Mul : Operator(NumericOpcode.I64Mul)

    data object I64DivS : Operator(NumericOpcode.I64DivS)

    data object I64DivU : Operator(NumericOpcode.I64DivU)

    data object I64RemS : Operator(NumericOpcode.I64RemS)

    data object I64RemU : Operator(NumericOpcode.I64RemU)

    data object I64And : Operator(NumericOpcode.I64And)

    data object I64Or : Operator(NumericOpcode.I64Or)

    data object I64Xor : Operator(NumericOpcode.I64Xor)

    data object I64Shl : Operator(NumericOpcode.I64Shl)

    data object I64ShrS : Operator(NumericOpcode.I64ShrS)

    data object I64ShrU : Operator(NumericOpcode.I64ShrU)

    data object I64Rotl : Operator(NumericOpcode.I64Rotl)

    data object I64Rotr : Operator(NumericOpcode.I64Rotr)

    data object F32Abs : Operator(NumericOpcode.F32Abs)

    data object F32Neg : Operator(NumericOpcode.F32Neg)

    data object F32Ceil : Operator(NumericOpcode.F32Ceil)

    data object F32Floor : Operator(NumericOpcode.F32Floor)

    data object F32Trunc : Operator(NumericOpcode.F32Trunc)

    data object F32Nearest : Operator(NumericOpcode.F32Nearest)

    data object F32Sqrt : Operator(NumericOpcode.F32Sqrt)

    data object F32Add : Operator(NumericOpcode.F32Add)

    data object F32Sub : Operator(NumericOpcode.F32Sub)

    data object F32Mul : Operator(NumericOpcode.F32Mul)

    data object F32Div : Operator(NumericOpcode.F32Div)

    data object F32Min : Operator(NumericOpcode.F32Min)

    data object F32Max : Operator(NumericOpcode.F32Max)

    data object F32Copysign : Operator(NumericOpcode.F32Copysign)

    data object F64Abs : Operator(NumericOpcode.F64Abs)

    data object F64Neg : Operator(NumericOpcode.F64Neg)

    data object F64Ceil : Operator(NumericOpcode.F64Ceil)

    data object F64Floor : Operator(NumericOpcode.F64Floor)

    data object F64Trunc : Operator(NumericOpcode.F64Trunc)

    data object F64Nearest : Operator(NumericOpcode.F64Nearest)

    data object F64Sqrt : Operator(NumericOpcode.F64Sqrt)

    data object F64Add : Operator(NumericOpcode.F64Add)

    data object F64Sub : Operator(NumericOpcode.F64Sub)

    data object F64Mul : Operator(NumericOpcode.F64Mul)

    data object F64Div : Operator(NumericOpcode.F64Div)

    data object F64Min : Operator(NumericOpcode.F64Min)

    data object F64Max : Operator(NumericOpcode.F64Max)

    data object F64Copysign : Operator(NumericOpcode.F64Copysign)

    data object I32WrapI64 : Operator(NumericOpcode.I32WrapI64)

    data object I32TruncF32S : Operator(NumericOpcode.I32TruncF32S)

    data object I32TruncF32U : Operator(NumericOpcode.I32TruncF32U)

    data object I32TruncF64S : Operator(NumericOpcode.I32TruncF64S)

    data object I32TruncF64U : Operator(NumericOpcode.I32TruncF64U)

    data object I64ExtendI32S : Operator(NumericOpcode.I64ExtendI32S)

    data object I64ExtendI32U : Operator(NumericOpcode.I64ExtendI32U)

    data object I64TruncF32S : Operator(NumericOpcode.I64TruncF32S)

    data object I64TruncF32U : Operator(NumericOpcode.I64TruncF32U)

    data object I64TruncF64S : Operator(NumericOpcode.I64TruncF64S)

    data object I64TruncF64U : Operator(NumericOpcode.I64TruncF64U)

    data object F32ConvertI32S : Operator(NumericOpcode.F32ConvertI32S)

    data object F32ConvertI32U : Operator(NumericOpcode.F32ConvertI32U)

    data object F32ConvertI64S : Operator(NumericOpcode.F32ConvertI64S)

    data object F32ConvertI64U : Operator(NumericOpcode.F32ConvertI64U)

    data object F32DemoteF64 : Operator(NumericOpcode.F32DemoteF64)

    data object F64ConvertI32S : Operator(NumericOpcode.F64ConvertI32S)

    data object F64ConvertI32U : Operator(NumericOpcode.F64ConvertI32U)

    data object F64ConvertI64S : Operator(NumericOpcode.F64ConvertI64S)

    data object F64ConvertI64U : Operator(NumericOpcode.F64ConvertI64U)

    data object F64PromoteF32 : Operator(NumericOpcode.F64PromoteF32)

    data object I32ReinterpretF32 : Operator(NumericOpcode.I32ReinterpretF32)

    data object I64ReinterpretF64 : Operator(NumericOpcode.I64ReinterpretF64)

    data object F32ReinterpretI32 : Operator(NumericOpcode.F32ReinterpretI32)

    data object F64ReinterpretI64 : Operator(NumericOpcode.F64ReinterpretI64)

    data object I32Extend8S : Operator(NumericOpcode.I32Extend8S)

    data object I32Extend16S : Operator(NumericOpcode.I32Extend16S)

    data object I64Extend8S : Operator(NumericOpcode.I64Extend8S)

    data object I64Extend16S : Operator(NumericOpcode.I64Extend16S)

    data object I64Extend32S : Operator(NumericOpcode.I64Extend32S)

    data object I32TruncSatF32S : Operator(NumericOpcode.I32TruncSatF32S)

    data object I32TruncSatF32U : Operator(NumericOpcode.I32TruncSatF32U)

    data object I32TruncSatF64S : Operator(NumericOpcode.I32TruncSatF64S)

    data object I32TruncSatF64U : Operator(NumericOpcode.I32TruncSatF64U)

    data object I64TruncSatF32S : Operator(NumericOpcode.I64TruncSatF32S)

    data object I64TruncSatF32U : Operator(NumericOpcode.I64TruncSatF32U)

    data object I64TruncSatF64S : Operator(NumericOpcode.I64TruncSatF64S)

    data object I64TruncSatF64U : Operator(NumericOpcode.I64TruncSatF64U)

    data object I64Add128 : Operator(NumericOpcode.I64Add128)

    data object I64Sub128 : Operator(NumericOpcode.I64Sub128)

    data object I64MulWideS : Operator(NumericOpcode.I64MulWideS)

    data object I64MulWideU : Operator(NumericOpcode.I64MulWideU)
}

enum class NumericOpcode {
    I32Eqz,
    I32Eq,
    I32Ne,
    I32LtS,
    I32LtU,
    I32GtS,
    I32GtU,
    I32LeS,
    I32LeU,
    I32GeS,
    I32GeU,
    I64Eqz,
    I64Eq,
    I64Ne,
    I64LtS,
    I64LtU,
    I64GtS,
    I64GtU,
    I64LeS,
    I64LeU,
    I64GeS,
    I64GeU,
    F32Eq,
    F32Ne,
    F32Lt,
    F32Gt,
    F32Le,
    F32Ge,
    F64Eq,
    F64Ne,
    F64Lt,
    F64Gt,
    F64Le,
    F64Ge,
    I32Clz,
    I32Ctz,
    I32Popcnt,
    I32Add,
    I32Sub,
    I32Mul,
    I32DivS,
    I32DivU,
    I32RemS,
    I32RemU,
    I32And,
    I32Or,
    I32Xor,
    I32Shl,
    I32ShrS,
    I32ShrU,
    I32Rotl,
    I32Rotr,
    I64Clz,
    I64Ctz,
    I64Popcnt,
    I64Add,
    I64Sub,
    I64Mul,
    I64DivS,
    I64DivU,
    I64RemS,
    I64RemU,
    I64And,
    I64Or,
    I64Xor,
    I64Shl,
    I64ShrS,
    I64ShrU,
    I64Rotl,
    I64Rotr,
    F32Abs,
    F32Neg,
    F32Ceil,
    F32Floor,
    F32Trunc,
    F32Nearest,
    F32Sqrt,
    F32Add,
    F32Sub,
    F32Mul,
    F32Div,
    F32Min,
    F32Max,
    F32Copysign,
    F64Abs,
    F64Neg,
    F64Ceil,
    F64Floor,
    F64Trunc,
    F64Nearest,
    F64Sqrt,
    F64Add,
    F64Sub,
    F64Mul,
    F64Div,
    F64Min,
    F64Max,
    F64Copysign,
    I32WrapI64,
    I32TruncF32S,
    I32TruncF32U,
    I32TruncF64S,
    I32TruncF64U,
    I64ExtendI32S,
    I64ExtendI32U,
    I64TruncF32S,
    I64TruncF32U,
    I64TruncF64S,
    I64TruncF64U,
    F32ConvertI32S,
    F32ConvertI32U,
    F32ConvertI64S,
    F32ConvertI64U,
    F32DemoteF64,
    F64ConvertI32S,
    F64ConvertI32U,
    F64ConvertI64S,
    F64ConvertI64U,
    F64PromoteF32,
    I32ReinterpretF32,
    I64ReinterpretF64,
    F32ReinterpretI32,
    F64ReinterpretI64,
    I32Extend8S,
    I32Extend16S,
    I64Extend8S,
    I64Extend16S,
    I64Extend32S,
    I32TruncSatF32S,
    I32TruncSatF32U,
    I32TruncSatF64S,
    I32TruncSatF64U,
    I64TruncSatF32S,
    I64TruncSatF32U,
    I64TruncSatF64S,
    I64TruncSatF64U,
    I64Add128,
    I64Sub128,
    I64MulWideS,
    I64MulWideU,
}
