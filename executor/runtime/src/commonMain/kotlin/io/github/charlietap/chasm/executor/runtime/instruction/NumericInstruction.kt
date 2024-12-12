package io.github.charlietap.chasm.executor.runtime.instruction

import kotlin.jvm.JvmInline

sealed interface NumericInstruction : ExecutionInstruction {

    @JvmInline
    value class I32Const(val value: Int) : NumericInstruction

    @JvmInline
    value class I64Const(val value: Long) : NumericInstruction

    @JvmInline
    value class F32Const(val value: Float) : NumericInstruction

    @JvmInline
    value class F64Const(val value: Double) : NumericInstruction

    data object I32Eqz : NumericInstruction

    data object I32Eq : NumericInstruction

    data object I32Ne : NumericInstruction

    data object I32LtS : NumericInstruction

    data object I32LtU : NumericInstruction

    data object I32GtS : NumericInstruction

    data object I32GtU : NumericInstruction

    data object I32LeS : NumericInstruction

    data object I32LeU : NumericInstruction

    data object I32GeS : NumericInstruction

    data object I32GeU : NumericInstruction

    data object I64Eqz : NumericInstruction

    data object I64Eq : NumericInstruction

    data object I64Ne : NumericInstruction

    data object I64LtS : NumericInstruction

    data object I64LtU : NumericInstruction

    data object I64GtS : NumericInstruction

    data object I64GtU : NumericInstruction

    data object I64LeS : NumericInstruction

    data object I64LeU : NumericInstruction

    data object I64GeS : NumericInstruction

    data object I64GeU : NumericInstruction

    data object F32Eq : NumericInstruction

    data object F32Ne : NumericInstruction

    data object F32Lt : NumericInstruction

    data object F32Gt : NumericInstruction

    data object F32Le : NumericInstruction

    data object F32Ge : NumericInstruction

    data object F64Eq : NumericInstruction

    data object F64Ne : NumericInstruction

    data object F64Lt : NumericInstruction

    data object F64Gt : NumericInstruction

    data object F64Le : NumericInstruction

    data object F64Ge : NumericInstruction

    data object I32Clz : NumericInstruction

    data object I32Ctz : NumericInstruction

    data object I32Popcnt : NumericInstruction

    data object I32Add : NumericInstruction

    data object I32Sub : NumericInstruction

    data object I32Mul : NumericInstruction

    data object I32DivS : NumericInstruction

    data object I32DivU : NumericInstruction

    data object I32RemS : NumericInstruction

    data object I32RemU : NumericInstruction

    data object I32And : NumericInstruction

    data object I32Or : NumericInstruction

    data object I32Xor : NumericInstruction

    data object I32Shl : NumericInstruction

    data object I32ShrS : NumericInstruction

    data object I32ShrU : NumericInstruction

    data object I32Rotl : NumericInstruction

    data object I32Rotr : NumericInstruction

    data object I64Clz : NumericInstruction

    data object I64Ctz : NumericInstruction

    data object I64Popcnt : NumericInstruction

    data object I64Add : NumericInstruction

    data object I64Sub : NumericInstruction

    data object I64Mul : NumericInstruction

    data object I64DivS : NumericInstruction

    data object I64DivU : NumericInstruction

    data object I64RemS : NumericInstruction

    data object I64RemU : NumericInstruction

    data object I64And : NumericInstruction

    data object I64Or : NumericInstruction

    data object I64Xor : NumericInstruction

    data object I64Shl : NumericInstruction

    data object I64ShrS : NumericInstruction

    data object I64ShrU : NumericInstruction

    data object I64Rotl : NumericInstruction

    data object I64Rotr : NumericInstruction

    data object F32Abs : NumericInstruction

    data object F32Neg : NumericInstruction

    data object F32Ceil : NumericInstruction

    data object F32Floor : NumericInstruction

    data object F32Trunc : NumericInstruction

    data object F32Nearest : NumericInstruction

    data object F32Sqrt : NumericInstruction

    data object F32Add : NumericInstruction

    data object F32Sub : NumericInstruction

    data object F32Mul : NumericInstruction

    data object F32Div : NumericInstruction

    data object F32Min : NumericInstruction

    data object F32Max : NumericInstruction

    data object F32Copysign : NumericInstruction

    data object F64Abs : NumericInstruction

    data object F64Neg : NumericInstruction

    data object F64Ceil : NumericInstruction

    data object F64Floor : NumericInstruction

    data object F64Trunc : NumericInstruction

    data object F64Nearest : NumericInstruction

    data object F64Sqrt : NumericInstruction

    data object F64Add : NumericInstruction

    data object F64Sub : NumericInstruction

    data object F64Mul : NumericInstruction

    data object F64Div : NumericInstruction

    data object F64Min : NumericInstruction

    data object F64Max : NumericInstruction

    data object F64Copysign : NumericInstruction

    data object I32WrapI64 : NumericInstruction

    data object I32TruncF32S : NumericInstruction

    data object I32TruncF32U : NumericInstruction

    data object I32TruncF64S : NumericInstruction

    data object I32TruncF64U : NumericInstruction

    data object I64ExtendI32S : NumericInstruction

    data object I64ExtendI32U : NumericInstruction

    data object I64TruncF32S : NumericInstruction

    data object I64TruncF32U : NumericInstruction

    data object I64TruncF64S : NumericInstruction

    data object I64TruncF64U : NumericInstruction

    data object F32ConvertI32S : NumericInstruction

    data object F32ConvertI32U : NumericInstruction

    data object F32ConvertI64S : NumericInstruction

    data object F32ConvertI64U : NumericInstruction

    data object F32DemoteF64 : NumericInstruction

    data object F64ConvertI32S : NumericInstruction

    data object F64ConvertI32U : NumericInstruction

    data object F64ConvertI64S : NumericInstruction

    data object F64ConvertI64U : NumericInstruction

    data object F64PromoteF32 : NumericInstruction

    data object I32ReinterpretF32 : NumericInstruction

    data object I64ReinterpretF64 : NumericInstruction

    data object F32ReinterpretI32 : NumericInstruction

    data object F64ReinterpretI64 : NumericInstruction

    data object I32Extend8S : NumericInstruction

    data object I32Extend16S : NumericInstruction

    data object I64Extend8S : NumericInstruction

    data object I64Extend16S : NumericInstruction

    data object I64Extend32S : NumericInstruction

    data object I32TruncSatF32S : NumericInstruction

    data object I32TruncSatF32U : NumericInstruction

    data object I32TruncSatF64S : NumericInstruction

    data object I32TruncSatF64U : NumericInstruction

    data object I64TruncSatF32S : NumericInstruction

    data object I64TruncSatF32U : NumericInstruction

    data object I64TruncSatF64S : NumericInstruction

    data object I64TruncSatF64U : NumericInstruction
}
