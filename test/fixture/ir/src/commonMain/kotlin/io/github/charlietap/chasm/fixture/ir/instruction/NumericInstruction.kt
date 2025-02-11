package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.NumericInstruction

fun numericInstruction(): NumericInstruction = i32ConstInstruction()

fun i32ConstInstruction(
    value: Int = 0,
) = NumericInstruction.I32Const(value)

fun i64ConstInstruction(
    value: Long = 0L,
) = NumericInstruction.I64Const(value)

fun f32ConstInstruction(
    value: Float = 0f,
    bits: Int = 0,
) = NumericInstruction.F32Const(value, bits)

fun f64ConstInstruction(
    value: Double = 0.0,
    bits: Long = 0,
) = NumericInstruction.F64Const(value, bits)

fun i32EqzInstruction() = NumericInstruction.I32Eqz

fun i32EqInstruction() = NumericInstruction.I32Eq

fun i32NeInstruction() = NumericInstruction.I32Ne

fun i32LtSInstruction() = NumericInstruction.I32LtS

fun i32LtUInstruction() = NumericInstruction.I32LtU

fun i32GtSInstruction() = NumericInstruction.I32GtS

fun i32GtUInstruction() = NumericInstruction.I32GtU

fun i32LeSInstruction() = NumericInstruction.I32LeS

fun i32LeUInstruction() = NumericInstruction.I32LeU

fun i32GeSInstruction() = NumericInstruction.I32GeS

fun i32GeUInstruction() = NumericInstruction.I32GeU

fun i64EqzInstruction() = NumericInstruction.I64Eqz

fun i64EqInstruction() = NumericInstruction.I64Eq

fun i64NeInstruction() = NumericInstruction.I64Ne

fun i64LtSInstruction() = NumericInstruction.I64LtS

fun i64LtUInstruction() = NumericInstruction.I64LtU

fun i64GtSInstruction() = NumericInstruction.I64GtS

fun i64GtUInstruction() = NumericInstruction.I64GtU

fun i64LeSInstruction() = NumericInstruction.I64LeS

fun i64LeUInstruction() = NumericInstruction.I64LeU

fun i64GeSInstruction() = NumericInstruction.I64GeS

fun i64GeUInstruction() = NumericInstruction.I64GeU

fun f32EqInstruction() = NumericInstruction.F32Eq

fun f32NeInstruction() = NumericInstruction.F32Ne

fun f32LtInstruction() = NumericInstruction.F32Lt

fun f32GtInstruction() = NumericInstruction.F32Gt

fun f32LeInstruction() = NumericInstruction.F32Le

fun f32GeInstruction() = NumericInstruction.F32Ge

fun f64EqInstruction() = NumericInstruction.F64Eq

fun f64NeInstruction() = NumericInstruction.F64Ne

fun f64LtInstruction() = NumericInstruction.F64Lt

fun f64GtInstruction() = NumericInstruction.F64Gt

fun f64LeInstruction() = NumericInstruction.F64Le

fun f64GeInstruction() = NumericInstruction.F64Ge

fun i32ClzInstruction() = NumericInstruction.I32Clz

fun i32CtzInstruction() = NumericInstruction.I32Ctz

fun i32PopcntInstruction() = NumericInstruction.I32Popcnt

fun i32AddInstruction() = NumericInstruction.I32Add

fun i32SubInstruction() = NumericInstruction.I32Sub

fun i32MulInstruction() = NumericInstruction.I32Mul

fun i32DivSInstruction() = NumericInstruction.I32DivS

fun i32DivUInstruction() = NumericInstruction.I32DivU

fun i32RemSInstruction() = NumericInstruction.I32RemS

fun i32RemUInstruction() = NumericInstruction.I32RemU

fun i32AndInstruction() = NumericInstruction.I32And

fun i32OrInstruction() = NumericInstruction.I32Or

fun i32XorInstruction() = NumericInstruction.I32Xor

fun i32ShlInstruction() = NumericInstruction.I32Shl

fun i32ShrSInstruction() = NumericInstruction.I32ShrS

fun i32ShrUInstruction() = NumericInstruction.I32ShrU

fun i32RotlInstruction() = NumericInstruction.I32Rotl

fun i32RotrInstruction() = NumericInstruction.I32Rotr

fun i64ClzInstruction() = NumericInstruction.I64Clz

fun i64CtzInstruction() = NumericInstruction.I64Ctz

fun i64PopcntInstruction() = NumericInstruction.I64Popcnt

fun i64AddInstruction() = NumericInstruction.I64Add

fun i64SubInstruction() = NumericInstruction.I64Sub

fun i64MulInstruction() = NumericInstruction.I64Mul

fun i64DivSInstruction() = NumericInstruction.I64DivS

fun i64DivUInstruction() = NumericInstruction.I64DivU

fun i64RemSInstruction() = NumericInstruction.I64RemS

fun i64RemUInstruction() = NumericInstruction.I64RemU

fun i64AndInstruction() = NumericInstruction.I64And

fun i64OrInstruction() = NumericInstruction.I64Or

fun i64XorInstruction() = NumericInstruction.I64Xor

fun i64ShlInstruction() = NumericInstruction.I64Shl

fun i64ShrSInstruction() = NumericInstruction.I64ShrS

fun i64ShrUInstruction() = NumericInstruction.I64ShrU

fun i64RotlInstruction() = NumericInstruction.I64Rotl

fun i64RotrInstruction() = NumericInstruction.I64Rotr

fun f32AbsInstruction() = NumericInstruction.F32Abs

fun f32NegInstruction() = NumericInstruction.F32Neg

fun f32CeilInstruction() = NumericInstruction.F32Ceil

fun f32FloorInstruction() = NumericInstruction.F32Floor

fun f32TruncInstruction() = NumericInstruction.F32Trunc

fun f32NearestInstruction() = NumericInstruction.F32Nearest

fun f32SqrtInstruction() = NumericInstruction.F32Sqrt

fun f32AddInstruction() = NumericInstruction.F32Add

fun f32SubInstruction() = NumericInstruction.F32Sub

fun f32MulInstruction() = NumericInstruction.F32Mul

fun f32DivInstruction() = NumericInstruction.F32Div

fun f32MinInstruction() = NumericInstruction.F32Min

fun f32MaxInstruction() = NumericInstruction.F32Max

fun f32CopysignInstruction() = NumericInstruction.F32Copysign

fun f64AbsInstruction() = NumericInstruction.F64Abs

fun f64NegInstruction() = NumericInstruction.F64Neg

fun f64CeilInstruction() = NumericInstruction.F64Ceil

fun f64FloorInstruction() = NumericInstruction.F64Floor

fun f64TruncInstruction() = NumericInstruction.F64Trunc

fun f64NearestInstruction() = NumericInstruction.F64Nearest

fun f64SqrtInstruction() = NumericInstruction.F64Sqrt

fun f64AddInstruction() = NumericInstruction.F64Add

fun f64SubInstruction() = NumericInstruction.F64Sub

fun f64MulInstruction() = NumericInstruction.F64Mul

fun f64DivInstruction() = NumericInstruction.F64Div

fun f64MinInstruction() = NumericInstruction.F64Min
