package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun numericRuntimeInstruction(): NumericInstruction = i32ConstRuntimeInstruction()

fun i32ConstRuntimeInstruction(
    value: Int = 0,
) = NumericInstruction.I32Const(value)

fun i64ConstRuntimeInstruction(
    value: Long = 0L,
) = NumericInstruction.I64Const(value)

fun f32ConstRuntimeInstruction(
    value: Float = 0f,
) = NumericInstruction.F32Const(value)

fun f64ConstRuntimeInstruction(
    value: Double = 0.0,
) = NumericInstruction.F64Const(value)

fun i32EqzRuntimeInstruction() = NumericInstruction.I32Eqz

fun i32EqRuntimeInstruction() = NumericInstruction.I32Eq

fun i32NeRuntimeInstruction() = NumericInstruction.I32Ne

fun i32LtSRuntimeInstruction() = NumericInstruction.I32LtS

fun i32LtURuntimeInstruction() = NumericInstruction.I32LtU

fun i32GtSRuntimeInstruction() = NumericInstruction.I32GtS

fun i32GtURuntimeInstruction() = NumericInstruction.I32GtU

fun i32LeSRuntimeInstruction() = NumericInstruction.I32LeS

fun i32LeURuntimeInstruction() = NumericInstruction.I32LeU

fun i32GeSRuntimeInstruction() = NumericInstruction.I32GeS

fun i32GeURuntimeInstruction() = NumericInstruction.I32GeU

fun i64EqzRuntimeInstruction() = NumericInstruction.I64Eqz

fun i64EqRuntimeInstruction() = NumericInstruction.I64Eq

fun i64NeRuntimeInstruction() = NumericInstruction.I64Ne

fun i64LtSRuntimeInstruction() = NumericInstruction.I64LtS

fun i64LtURuntimeInstruction() = NumericInstruction.I64LtU

fun i64GtSRuntimeInstruction() = NumericInstruction.I64GtS

fun i64GtURuntimeInstruction() = NumericInstruction.I64GtU

fun i64LeSRuntimeInstruction() = NumericInstruction.I64LeS

fun i64LeURuntimeInstruction() = NumericInstruction.I64LeU

fun i64GeSRuntimeInstruction() = NumericInstruction.I64GeS

fun i64GeURuntimeInstruction() = NumericInstruction.I64GeU

fun f32EqRuntimeInstruction() = NumericInstruction.F32Eq

fun f32NeRuntimeInstruction() = NumericInstruction.F32Ne

fun f32LtRuntimeInstruction() = NumericInstruction.F32Lt

fun f32GtRuntimeInstruction() = NumericInstruction.F32Gt

fun f32LeRuntimeInstruction() = NumericInstruction.F32Le

fun f32GeRuntimeInstruction() = NumericInstruction.F32Ge

fun f64EqRuntimeInstruction() = NumericInstruction.F64Eq

fun f64NeRuntimeInstruction() = NumericInstruction.F64Ne

fun f64LtRuntimeInstruction() = NumericInstruction.F64Lt

fun f64GtRuntimeInstruction() = NumericInstruction.F64Gt

fun f64LeRuntimeInstruction() = NumericInstruction.F64Le

fun f64GeRuntimeInstruction() = NumericInstruction.F64Ge

fun i32ClzRuntimeInstruction() = NumericInstruction.I32Clz

fun i32CtzRuntimeInstruction() = NumericInstruction.I32Ctz

fun i32PopcntRuntimeInstruction() = NumericInstruction.I32Popcnt

fun i32AddRuntimeInstruction() = NumericInstruction.I32Add

fun i32SubRuntimeInstruction() = NumericInstruction.I32Sub

fun i32MulRuntimeInstruction() = NumericInstruction.I32Mul

fun i32DivSRuntimeInstruction() = NumericInstruction.I32DivS

fun i32DivURuntimeInstruction() = NumericInstruction.I32DivU

fun i32RemSRuntimeInstruction() = NumericInstruction.I32RemS

fun i32RemURuntimeInstruction() = NumericInstruction.I32RemU

fun i32AndRuntimeInstruction() = NumericInstruction.I32And

fun i32OrRuntimeInstruction() = NumericInstruction.I32Or

fun i32XorRuntimeInstruction() = NumericInstruction.I32Xor

fun i32ShlRuntimeInstruction() = NumericInstruction.I32Shl

fun i32ShrSRuntimeInstruction() = NumericInstruction.I32ShrS

fun i32ShrURuntimeInstruction() = NumericInstruction.I32ShrU

fun i32RotlRuntimeInstruction() = NumericInstruction.I32Rotl

fun i32RotrRuntimeInstruction() = NumericInstruction.I32Rotr

fun i64ClzRuntimeInstruction() = NumericInstruction.I64Clz

fun i64CtzRuntimeInstruction() = NumericInstruction.I64Ctz

fun i64PopcntRuntimeInstruction() = NumericInstruction.I64Popcnt

fun i64AddRuntimeInstruction() = NumericInstruction.I64Add

fun i64SubRuntimeInstruction() = NumericInstruction.I64Sub

fun i64MulRuntimeInstruction() = NumericInstruction.I64Mul

fun i64DivSRuntimeInstruction() = NumericInstruction.I64DivS

fun i64DivURuntimeInstruction() = NumericInstruction.I64DivU

fun i64RemSRuntimeInstruction() = NumericInstruction.I64RemS

fun i64RemURuntimeInstruction() = NumericInstruction.I64RemU

fun i64AndRuntimeInstruction() = NumericInstruction.I64And

fun i64OrRuntimeInstruction() = NumericInstruction.I64Or

fun i64XorRuntimeInstruction() = NumericInstruction.I64Xor

fun i64ShlRuntimeInstruction() = NumericInstruction.I64Shl

fun i64ShrSRuntimeInstruction() = NumericInstruction.I64ShrS

fun i64ShrURuntimeInstruction() = NumericInstruction.I64ShrU

fun i64RotlRuntimeInstruction() = NumericInstruction.I64Rotl

fun i64RotrRuntimeInstruction() = NumericInstruction.I64Rotr

fun f32AbsRuntimeInstruction() = NumericInstruction.F32Abs

fun f32NegRuntimeInstruction() = NumericInstruction.F32Neg

fun f32CeilRuntimeInstruction() = NumericInstruction.F32Ceil

fun f32FloorRuntimeInstruction() = NumericInstruction.F32Floor

fun f32TruncRuntimeInstruction() = NumericInstruction.F32Trunc

fun f32NearestRuntimeInstruction() = NumericInstruction.F32Nearest

fun f32SqrtRuntimeInstruction() = NumericInstruction.F32Sqrt

fun f32AddRuntimeInstruction() = NumericInstruction.F32Add

fun f32SubRuntimeInstruction() = NumericInstruction.F32Sub

fun f32MulRuntimeInstruction() = NumericInstruction.F32Mul

fun f32DivRuntimeInstruction() = NumericInstruction.F32Div

fun f32MinRuntimeInstruction() = NumericInstruction.F32Min

fun f32MaxRuntimeInstruction() = NumericInstruction.F32Max

fun f32CopysignRuntimeInstruction() = NumericInstruction.F32Copysign

fun f64AbsRuntimeInstruction() = NumericInstruction.F64Abs

fun f64NegRuntimeInstruction() = NumericInstruction.F64Neg

fun f64CeilRuntimeInstruction() = NumericInstruction.F64Ceil

fun f64FloorRuntimeInstruction() = NumericInstruction.F64Floor

fun f64TruncRuntimeInstruction() = NumericInstruction.F64Trunc

fun f64NearestRuntimeInstruction() = NumericInstruction.F64Nearest

fun f64SqrtRuntimeInstruction() = NumericInstruction.F64Sqrt

fun f64AddRuntimeInstruction() = NumericInstruction.F64Add

fun f64SubRuntimeInstruction() = NumericInstruction.F64Sub

fun f64MulRuntimeInstruction() = NumericInstruction.F64Mul

fun f64DivRuntimeInstruction() = NumericInstruction.F64Div

fun f64MinRuntimeInstruction() = NumericInstruction.F64Min
