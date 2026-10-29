package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Add
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.And
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Copysign
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Div
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.DivS
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.DivU
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Max
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Min
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Mul
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Or
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.RemS
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.RemU
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Rotl
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Rotr
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Shl
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.ShrS
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.ShrU
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Sub
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation.Xor
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Eq
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Ge
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.GeS
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.GeU
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Gt
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.GtS
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.GtU
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Le
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.LeS
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.LeU
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Lt
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.LtS
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.LtU
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison.Ne
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F32ConvertI32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F32ConvertI32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F32ConvertI64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F32ConvertI64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F32DemoteF64
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F64ConvertI32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F64ConvertI32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F64ConvertI64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F64ConvertI64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.F64PromoteF32
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncF32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncF32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncF64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncF64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncSatF32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncSatF32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncSatF64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32TruncSatF64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I32WrapI64
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64ExtendI32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64ExtendI32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncF32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncF32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncF64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncF64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncSatF32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncSatF32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncSatF64S
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion.I64TruncSatF64U
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Abs
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Ceil
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Clz
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Ctz
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Extend16S
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Extend32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Extend8S
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Floor
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Nearest
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Neg
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Popcnt
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Sqrt
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation.Trunc
import io.github.charlietap.chasm.compiler.backend.analysis.form.WideOperation.Add128
import io.github.charlietap.chasm.compiler.backend.analysis.form.WideOperation.MulWideS
import io.github.charlietap.chasm.compiler.backend.analysis.form.WideOperation.MulWideU
import io.github.charlietap.chasm.compiler.backend.analysis.form.WideOperation.Sub128
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.NumberType.F32
import io.github.charlietap.chasm.type.NumberType.F64
import io.github.charlietap.chasm.type.NumberType.I32
import io.github.charlietap.chasm.type.NumberType.I64

fun NumericInstruction.form(): NumericForm = when (opcode / OPCODES_PER_GROUP) {
    0 -> form0()
    1 -> form1()
    2 -> form2()
    3 -> form3()
    4 -> form4()
    5 -> form5()
    6 -> form6()
    7 -> form7()
    8 -> form8()
    9 -> form9()
    10 -> form10()
    11 -> form11()
    12 -> form12()
    13 -> form13()
    14 -> form14()
    15 -> form15()
    16 -> form16()
    17 -> form17()
    18 -> form18()
    19 -> form19()
    20 -> form20()
    21 -> form21()
    22 -> form22()
    else -> unknown(this)
}

private const val OPCODES_PER_GROUP = 20

private fun unknown(
    instruction: NumericInstruction,
): Nothing = error("no form for numeric instruction $instruction")

private fun NumericInstruction.form0(): NumericForm = when (val i = this) {
    is NumericInstruction.I32ConstS -> NumericForm.Constant(I32, i.value.toLong(), i.destinationSlot)
    is NumericInstruction.I32BitFieldExtractS -> NumericForm.BitFieldExtract(slot(i.operandSlot), i.shift, i.mask, i.destinationSlot)
    is NumericInstruction.I64ConstS -> NumericForm.Constant(I64, i.value, i.destinationSlot)
    is NumericInstruction.F32ConstS -> NumericForm.Constant(F32, i.bits.toLong(), i.destinationSlot)
    is NumericInstruction.F64ConstS -> NumericForm.Constant(F64, i.bits, i.destinationSlot)

    is NumericInstruction.I32AddIs -> binary(I32, Add, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32AddSi -> binary(I32, Add, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32AddSs -> binary(I32, Add, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32SubIi -> binary(I32, Sub, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32SubIs -> binary(I32, Sub, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32SubSi -> binary(I32, Sub, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32SubSs -> binary(I32, Sub, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32MulIi -> binary(I32, Mul, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32MulIs -> binary(I32, Mul, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32MulSi -> binary(I32, Mul, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32MulSs -> binary(I32, Mul, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32DivSIi -> binary(I32, DivS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32DivSIs -> binary(I32, DivS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32DivSSi -> binary(I32, DivS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32DivSSs -> binary(I32, DivS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form1(): NumericForm = when (val i = this) {
    is NumericInstruction.I32DivUIi -> binary(I32, DivU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32DivUIs -> binary(I32, DivU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32DivUSi -> binary(I32, DivU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32DivUSs -> binary(I32, DivU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RemSIi -> binary(I32, RemS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RemSIs -> binary(I32, RemS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RemSSi -> binary(I32, RemS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RemSSs -> binary(I32, RemS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RemUIi -> binary(I32, RemU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RemUIs -> binary(I32, RemU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RemUSi -> binary(I32, RemU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RemUSs -> binary(I32, RemU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32AndIi -> binary(I32, And, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32AndIs -> binary(I32, And, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32AndSi -> binary(I32, And, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32AndSs -> binary(I32, And, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32OrIi -> binary(I32, Or, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32OrIs -> binary(I32, Or, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32OrSi -> binary(I32, Or, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32OrSs -> binary(I32, Or, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form2(): NumericForm = when (val i = this) {
    is NumericInstruction.I32XorIi -> binary(I32, Xor, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32XorIs -> binary(I32, Xor, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32XorSi -> binary(I32, Xor, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32XorSs -> binary(I32, Xor, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShlIi -> binary(I32, Shl, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShlIs -> binary(I32, Shl, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShlSi -> binary(I32, Shl, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShlSs -> binary(I32, Shl, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShrSIi -> binary(I32, ShrS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShrSIs -> binary(I32, ShrS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShrSSi -> binary(I32, ShrS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShrSSs -> binary(I32, ShrS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShrUIi -> binary(I32, ShrU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShrUIs -> binary(I32, ShrU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32ShrUSi -> binary(I32, ShrU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32ShrUSs -> binary(I32, ShrU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RotlIi -> binary(I32, Rotl, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RotlIs -> binary(I32, Rotl, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RotlSi -> binary(I32, Rotl, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RotlSs -> binary(I32, Rotl, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form3(): NumericForm = when (val i = this) {
    is NumericInstruction.I32RotrIi -> binary(I32, Rotr, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RotrIs -> binary(I32, Rotr, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32RotrSi -> binary(I32, Rotr, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32RotrSs -> binary(I32, Rotr, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)

    is NumericInstruction.I64AddIi -> binary(I64, Add, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64AddIs -> binary(I64, Add, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64AddSi -> binary(I64, Add, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64AddSs -> binary(I64, Add, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64SubIi -> binary(I64, Sub, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64SubIs -> binary(I64, Sub, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64SubSi -> binary(I64, Sub, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64SubSs -> binary(I64, Sub, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64MulIi -> binary(I64, Mul, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64MulIs -> binary(I64, Mul, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64MulSi -> binary(I64, Mul, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64MulSs -> binary(I64, Mul, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)

    is NumericInstruction.I64Add128Iiii ->
        wide(Add128, immediate(i.leftLow), immediate(i.leftHigh), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Iiis ->
        wide(Add128, immediate(i.leftLow), immediate(i.leftHigh), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Iisi ->
        wide(Add128, immediate(i.leftLow), immediate(i.leftHigh), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Iiss ->
        wide(Add128, immediate(i.leftLow), immediate(i.leftHigh), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form4(): NumericForm = when (val i = this) {
    is NumericInstruction.I64Add128Isii ->
        wide(Add128, immediate(i.leftLow), slot(i.leftHighSlot), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Isis ->
        wide(Add128, immediate(i.leftLow), slot(i.leftHighSlot), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Issi ->
        wide(Add128, immediate(i.leftLow), slot(i.leftHighSlot), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Isss ->
        wide(Add128, immediate(i.leftLow), slot(i.leftHighSlot), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Siii ->
        wide(Add128, slot(i.leftLowSlot), immediate(i.leftHigh), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Siis ->
        wide(Add128, slot(i.leftLowSlot), immediate(i.leftHigh), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Sisi ->
        wide(Add128, slot(i.leftLowSlot), immediate(i.leftHigh), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Siss ->
        wide(Add128, slot(i.leftLowSlot), immediate(i.leftHigh), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Ssii ->
        wide(Add128, slot(i.leftLowSlot), slot(i.leftHighSlot), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Ssis ->
        wide(Add128, slot(i.leftLowSlot), slot(i.leftHighSlot), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Sssi ->
        wide(Add128, slot(i.leftLowSlot), slot(i.leftHighSlot), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Add128Ssss ->
        wide(Add128, slot(i.leftLowSlot), slot(i.leftHighSlot), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Iiii ->
        wide(Sub128, immediate(i.leftLow), immediate(i.leftHigh), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Iiis ->
        wide(Sub128, immediate(i.leftLow), immediate(i.leftHigh), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Iisi ->
        wide(Sub128, immediate(i.leftLow), immediate(i.leftHigh), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Iiss ->
        wide(Sub128, immediate(i.leftLow), immediate(i.leftHigh), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Isii ->
        wide(Sub128, immediate(i.leftLow), slot(i.leftHighSlot), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Isis ->
        wide(Sub128, immediate(i.leftLow), slot(i.leftHighSlot), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Issi ->
        wide(Sub128, immediate(i.leftLow), slot(i.leftHighSlot), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Isss ->
        wide(Sub128, immediate(i.leftLow), slot(i.leftHighSlot), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form5(): NumericForm = when (val i = this) {
    is NumericInstruction.I64Sub128Siii ->
        wide(Sub128, slot(i.leftLowSlot), immediate(i.leftHigh), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Siis ->
        wide(Sub128, slot(i.leftLowSlot), immediate(i.leftHigh), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Sisi ->
        wide(Sub128, slot(i.leftLowSlot), immediate(i.leftHigh), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Siss ->
        wide(Sub128, slot(i.leftLowSlot), immediate(i.leftHigh), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Ssii ->
        wide(Sub128, slot(i.leftLowSlot), slot(i.leftHighSlot), immediate(i.rightLow), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Ssis ->
        wide(Sub128, slot(i.leftLowSlot), slot(i.leftHighSlot), immediate(i.rightLow), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Sssi ->
        wide(Sub128, slot(i.leftLowSlot), slot(i.leftHighSlot), slot(i.rightLowSlot), immediate(i.rightHigh), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64Sub128Ssss ->
        wide(Sub128, slot(i.leftLowSlot), slot(i.leftHighSlot), slot(i.rightLowSlot), slot(i.rightHighSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideSIi -> wide(MulWideS, immediate(i.left), immediate(i.right), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideSIs -> wide(MulWideS, immediate(i.left), slot(i.rightSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideSSi -> wide(MulWideS, slot(i.leftSlot), immediate(i.right), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideSSs -> wide(MulWideS, slot(i.leftSlot), slot(i.rightSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideUIi -> wide(MulWideU, immediate(i.left), immediate(i.right), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideUIs -> wide(MulWideU, immediate(i.left), slot(i.rightSlot), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideUSi -> wide(MulWideU, slot(i.leftSlot), immediate(i.right), i.destinationLowSlot, i.destinationHighSlot)
    is NumericInstruction.I64MulWideUSs -> wide(MulWideU, slot(i.leftSlot), slot(i.rightSlot), i.destinationLowSlot, i.destinationHighSlot)

    is NumericInstruction.I64DivSIi -> binary(I64, DivS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64DivSIs -> binary(I64, DivS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64DivSSi -> binary(I64, DivS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64DivSSs -> binary(I64, DivS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form6(): NumericForm = when (val i = this) {
    is NumericInstruction.I64DivUIi -> binary(I64, DivU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64DivUIs -> binary(I64, DivU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64DivUSi -> binary(I64, DivU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64DivUSs -> binary(I64, DivU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RemSIi -> binary(I64, RemS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RemSIs -> binary(I64, RemS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RemSSi -> binary(I64, RemS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RemSSs -> binary(I64, RemS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RemUIi -> binary(I64, RemU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RemUIs -> binary(I64, RemU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RemUSi -> binary(I64, RemU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RemUSs -> binary(I64, RemU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64AndIi -> binary(I64, And, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64AndIs -> binary(I64, And, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64AndSi -> binary(I64, And, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64AndSs -> binary(I64, And, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64OrIi -> binary(I64, Or, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64OrIs -> binary(I64, Or, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64OrSi -> binary(I64, Or, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64OrSs -> binary(I64, Or, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form7(): NumericForm = when (val i = this) {
    is NumericInstruction.I64XorIi -> binary(I64, Xor, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64XorIs -> binary(I64, Xor, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64XorSi -> binary(I64, Xor, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64XorSs -> binary(I64, Xor, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShlIi -> binary(I64, Shl, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShlIs -> binary(I64, Shl, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShlSi -> binary(I64, Shl, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShlSs -> binary(I64, Shl, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShrSIi -> binary(I64, ShrS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShrSIs -> binary(I64, ShrS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShrSSi -> binary(I64, ShrS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShrSSs -> binary(I64, ShrS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShrUIi -> binary(I64, ShrU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShrUIs -> binary(I64, ShrU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64ShrUSi -> binary(I64, ShrU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64ShrUSs -> binary(I64, ShrU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RotlIi -> binary(I64, Rotl, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RotlIs -> binary(I64, Rotl, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RotlSi -> binary(I64, Rotl, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RotlSs -> binary(I64, Rotl, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form8(): NumericForm = when (val i = this) {
    is NumericInstruction.I64RotrIi -> binary(I64, Rotr, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RotrIs -> binary(I64, Rotr, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64RotrSi -> binary(I64, Rotr, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64RotrSs -> binary(I64, Rotr, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)

    is NumericInstruction.I32EqzI -> compare(I32, Eq, immediate(i.operand), ZERO, i.destinationSlot)
    is NumericInstruction.I32EqzS -> compare(I32, Eq, slot(i.operandSlot), ZERO, i.destinationSlot)
    is NumericInstruction.I64EqzI -> compare(I64, Eq, immediate(i.operand), ZERO, i.destinationSlot)
    is NumericInstruction.I64EqzS -> compare(I64, Eq, slot(i.operandSlot), ZERO, i.destinationSlot)
    is NumericInstruction.I32ClzI -> unary(I32, Clz, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32ClzS -> unary(I32, Clz, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32CtzI -> unary(I32, Ctz, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32CtzS -> unary(I32, Ctz, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32PopcntI -> unary(I32, Popcnt, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32PopcntS -> unary(I32, Popcnt, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64ClzI -> unary(I64, Clz, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64ClzS -> unary(I64, Clz, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64CtzI -> unary(I64, Ctz, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64CtzS -> unary(I64, Ctz, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64PopcntI -> unary(I64, Popcnt, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64PopcntS -> unary(I64, Popcnt, slot(i.operandSlot), i.destinationSlot)

    else -> unknown(this)
}

private fun NumericInstruction.form9(): NumericForm = when (val i = this) {
    is NumericInstruction.I32EqIi -> compare(I32, Eq, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32EqIs -> compare(I32, Eq, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32EqSi -> compare(I32, Eq, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32EqSs -> compare(I32, Eq, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32NeIi -> compare(I32, Ne, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32NeIs -> compare(I32, Ne, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32NeSi -> compare(I32, Ne, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32NeSs -> compare(I32, Ne, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LtSIi -> compare(I32, LtS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LtSIs -> compare(I32, LtS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LtSSi -> compare(I32, LtS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LtSSs -> compare(I32, LtS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LtUIi -> compare(I32, LtU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LtUIs -> compare(I32, LtU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LtUSi -> compare(I32, LtU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LtUSs -> compare(I32, LtU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GtSIi -> compare(I32, GtS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GtSIs -> compare(I32, GtS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GtSSi -> compare(I32, GtS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GtSSs -> compare(I32, GtS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form10(): NumericForm = when (val i = this) {
    is NumericInstruction.I32GtUIi -> compare(I32, GtU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GtUIs -> compare(I32, GtU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GtUSi -> compare(I32, GtU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GtUSs -> compare(I32, GtU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LeSIi -> compare(I32, LeS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LeSIs -> compare(I32, LeS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LeSSi -> compare(I32, LeS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LeSSs -> compare(I32, LeS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LeUIi -> compare(I32, LeU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LeUIs -> compare(I32, LeU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32LeUSi -> compare(I32, LeU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32LeUSs -> compare(I32, LeU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GeSIi -> compare(I32, GeS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GeSIs -> compare(I32, GeS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GeSSi -> compare(I32, GeS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GeSSs -> compare(I32, GeS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GeUIi -> compare(I32, GeU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GeUIs -> compare(I32, GeU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I32GeUSi -> compare(I32, GeU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I32GeUSs -> compare(I32, GeU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form11(): NumericForm = when (val i = this) {
    is NumericInstruction.I64EqIi -> compare(I64, Eq, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64EqIs -> compare(I64, Eq, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64EqSi -> compare(I64, Eq, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64EqSs -> compare(I64, Eq, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64NeIi -> compare(I64, Ne, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64NeIs -> compare(I64, Ne, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64NeSi -> compare(I64, Ne, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64NeSs -> compare(I64, Ne, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LtSIi -> compare(I64, LtS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LtSIs -> compare(I64, LtS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LtSSi -> compare(I64, LtS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LtSSs -> compare(I64, LtS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LtUIi -> compare(I64, LtU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LtUIs -> compare(I64, LtU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LtUSi -> compare(I64, LtU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LtUSs -> compare(I64, LtU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GtSIi -> compare(I64, GtS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GtSIs -> compare(I64, GtS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GtSSi -> compare(I64, GtS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GtSSs -> compare(I64, GtS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form12(): NumericForm = when (val i = this) {
    is NumericInstruction.I64GtUIi -> compare(I64, GtU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GtUIs -> compare(I64, GtU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GtUSi -> compare(I64, GtU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GtUSs -> compare(I64, GtU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LeSIi -> compare(I64, LeS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LeSIs -> compare(I64, LeS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LeSSi -> compare(I64, LeS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LeSSs -> compare(I64, LeS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LeUIi -> compare(I64, LeU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LeUIs -> compare(I64, LeU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64LeUSi -> compare(I64, LeU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64LeUSs -> compare(I64, LeU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GeSIi -> compare(I64, GeS, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GeSIs -> compare(I64, GeS, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GeSSi -> compare(I64, GeS, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GeSSs -> compare(I64, GeS, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GeUIi -> compare(I64, GeU, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GeUIs -> compare(I64, GeU, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.I64GeUSi -> compare(I64, GeU, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.I64GeUSs -> compare(I64, GeU, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)

    else -> unknown(this)
}

private fun NumericInstruction.form13(): NumericForm = when (val i = this) {
    is NumericInstruction.I32Extend16SI -> unary(I32, Extend16S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32Extend16SS -> unary(I32, Extend16S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32Extend8SI -> unary(I32, Extend8S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32Extend8SS -> unary(I32, Extend8S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncF32SI -> convert(I32TruncF32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncF32SS -> convert(I32TruncF32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncF32UI -> convert(I32TruncF32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncF32US -> convert(I32TruncF32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncF64SI -> convert(I32TruncF64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncF64SS -> convert(I32TruncF64S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncF64UI -> convert(I32TruncF64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncF64US -> convert(I32TruncF64U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncSatF32SI -> convert(I32TruncSatF32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncSatF32SS -> convert(I32TruncSatF32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncSatF32UI -> convert(I32TruncSatF32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncSatF32US -> convert(I32TruncSatF32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncSatF64SI -> convert(I32TruncSatF64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncSatF64SS -> convert(I32TruncSatF64S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I32TruncSatF64UI -> convert(I32TruncSatF64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32TruncSatF64US -> convert(I32TruncSatF64U, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form14(): NumericForm = when (val i = this) {
    is NumericInstruction.I32WrapI64I -> convert(I32WrapI64, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I32WrapI64S -> convert(I32WrapI64, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64Extend16SI -> unary(I64, Extend16S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64Extend16SS -> unary(I64, Extend16S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64Extend32SI -> unary(I64, Extend32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64Extend32SS -> unary(I64, Extend32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64Extend8SI -> unary(I64, Extend8S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64Extend8SS -> unary(I64, Extend8S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64ExtendI32SI -> convert(I64ExtendI32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64ExtendI32SS -> convert(I64ExtendI32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64ExtendI32UI -> convert(I64ExtendI32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64ExtendI32US -> convert(I64ExtendI32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncF32SI -> convert(I64TruncF32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncF32SS -> convert(I64TruncF32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncF32UI -> convert(I64TruncF32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncF32US -> convert(I64TruncF32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncF64SI -> convert(I64TruncF64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncF64SS -> convert(I64TruncF64S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncF64UI -> convert(I64TruncF64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncF64US -> convert(I64TruncF64U, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form15(): NumericForm = when (val i = this) {
    is NumericInstruction.I64TruncSatF32SI -> convert(I64TruncSatF32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncSatF32SS -> convert(I64TruncSatF32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncSatF32UI -> convert(I64TruncSatF32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncSatF32US -> convert(I64TruncSatF32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncSatF64SI -> convert(I64TruncSatF64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncSatF64SS -> convert(I64TruncSatF64S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.I64TruncSatF64UI -> convert(I64TruncSatF64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.I64TruncSatF64US -> convert(I64TruncSatF64U, slot(i.operandSlot), i.destinationSlot)

    is NumericInstruction.F32AddIi -> binary(F32, Add, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32AddIs -> binary(F32, Add, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32AddSi -> binary(F32, Add, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32AddSs -> binary(F32, Add, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32SubIi -> binary(F32, Sub, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32SubIs -> binary(F32, Sub, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32SubSi -> binary(F32, Sub, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32SubSs -> binary(F32, Sub, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MulIi -> binary(F32, Mul, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MulIs -> binary(F32, Mul, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MulSi -> binary(F32, Mul, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MulSs -> binary(F32, Mul, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form16(): NumericForm = when (val i = this) {
    is NumericInstruction.F32DivIi -> binary(F32, Div, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32DivIs -> binary(F32, Div, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32DivSi -> binary(F32, Div, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32DivSs -> binary(F32, Div, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MinIi -> binary(F32, Min, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MinIs -> binary(F32, Min, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MinSi -> binary(F32, Min, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MinSs -> binary(F32, Min, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MaxIi -> binary(F32, Max, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MaxIs -> binary(F32, Max, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32MaxSi -> binary(F32, Max, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32MaxSs -> binary(F32, Max, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32CopysignIi -> binary(F32, Copysign, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32CopysignIs -> binary(F32, Copysign, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32CopysignSi -> binary(F32, Copysign, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32CopysignSs -> binary(F32, Copysign, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32AbsI -> unary(F32, Abs, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32AbsS -> unary(F32, Abs, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32NegI -> unary(F32, Neg, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32NegS -> unary(F32, Neg, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form17(): NumericForm = when (val i = this) {
    is NumericInstruction.F32CeilI -> unary(F32, Ceil, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32CeilS -> unary(F32, Ceil, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32FloorI -> unary(F32, Floor, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32FloorS -> unary(F32, Floor, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32TruncI -> unary(F32, Trunc, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32TruncS -> unary(F32, Trunc, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32NearestI -> unary(F32, Nearest, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32NearestS -> unary(F32, Nearest, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32SqrtI -> unary(F32, Sqrt, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32SqrtS -> unary(F32, Sqrt, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32EqIi -> compare(F32, Eq, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32EqIs -> compare(F32, Eq, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32EqSi -> compare(F32, Eq, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32EqSs -> compare(F32, Eq, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32NeIi -> compare(F32, Ne, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32NeIs -> compare(F32, Ne, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32NeSi -> compare(F32, Ne, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32NeSs -> compare(F32, Ne, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32LtIi -> compare(F32, Lt, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32LtIs -> compare(F32, Lt, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form18(): NumericForm = when (val i = this) {
    is NumericInstruction.F32LtSi -> compare(F32, Lt, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32LtSs -> compare(F32, Lt, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32GtIi -> compare(F32, Gt, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32GtIs -> compare(F32, Gt, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32GtSi -> compare(F32, Gt, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32GtSs -> compare(F32, Gt, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32LeIi -> compare(F32, Le, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32LeIs -> compare(F32, Le, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32LeSi -> compare(F32, Le, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32LeSs -> compare(F32, Le, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32GeIi -> compare(F32, Ge, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32GeIs -> compare(F32, Ge, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32GeSi -> compare(F32, Ge, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F32GeSs -> compare(F32, Ge, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F32ConvertI32SI -> convert(F32ConvertI32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32ConvertI32SS -> convert(F32ConvertI32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32ConvertI32UI -> convert(F32ConvertI32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32ConvertI32US -> convert(F32ConvertI32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32ConvertI64SI -> convert(F32ConvertI64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32ConvertI64SS -> convert(F32ConvertI64S, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form19(): NumericForm = when (val i = this) {
    is NumericInstruction.F32ConvertI64UI -> convert(F32ConvertI64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32ConvertI64US -> convert(F32ConvertI64U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F32DemoteF64I -> convert(F32DemoteF64, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F32DemoteF64S -> convert(F32DemoteF64, slot(i.operandSlot), i.destinationSlot)

    is NumericInstruction.F64AddIi -> binary(F64, Add, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64AddIs -> binary(F64, Add, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64AddSi -> binary(F64, Add, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64AddSs -> binary(F64, Add, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64SubIi -> binary(F64, Sub, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64SubIs -> binary(F64, Sub, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64SubSi -> binary(F64, Sub, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64SubSs -> binary(F64, Sub, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64MulIi -> binary(F64, Mul, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MulIs -> binary(F64, Mul, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64MulSi -> binary(F64, Mul, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MulSs -> binary(F64, Mul, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64DivIi -> binary(F64, Div, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64DivIs -> binary(F64, Div, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64DivSi -> binary(F64, Div, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64DivSs -> binary(F64, Div, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form20(): NumericForm = when (val i = this) {
    is NumericInstruction.F64MinIi -> binary(F64, Min, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MinIs -> binary(F64, Min, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64MinSi -> binary(F64, Min, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MinSs -> binary(F64, Min, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64MaxIi -> binary(F64, Max, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MaxIs -> binary(F64, Max, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64MaxSi -> binary(F64, Max, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64MaxSs -> binary(F64, Max, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64CopysignIi -> binary(F64, Copysign, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64CopysignIs -> binary(F64, Copysign, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64CopysignSi -> binary(F64, Copysign, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64CopysignSs -> binary(F64, Copysign, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64AbsI -> unary(F64, Abs, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64AbsS -> unary(F64, Abs, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64NegI -> unary(F64, Neg, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64NegS -> unary(F64, Neg, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64CeilI -> unary(F64, Ceil, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64CeilS -> unary(F64, Ceil, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64FloorI -> unary(F64, Floor, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64FloorS -> unary(F64, Floor, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form21(): NumericForm = when (val i = this) {
    is NumericInstruction.F64TruncI -> unary(F64, Trunc, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64TruncS -> unary(F64, Trunc, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64NearestI -> unary(F64, Nearest, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64NearestS -> unary(F64, Nearest, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64SqrtI -> unary(F64, Sqrt, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64SqrtS -> unary(F64, Sqrt, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64EqIi -> compare(F64, Eq, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64EqIs -> compare(F64, Eq, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64EqSi -> compare(F64, Eq, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64EqSs -> compare(F64, Eq, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64NeIi -> compare(F64, Ne, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64NeIs -> compare(F64, Ne, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64NeSi -> compare(F64, Ne, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64NeSs -> compare(F64, Ne, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64LtIi -> compare(F64, Lt, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64LtIs -> compare(F64, Lt, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64LtSi -> compare(F64, Lt, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64LtSs -> compare(F64, Lt, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64GtIi -> compare(F64, Gt, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64GtIs -> compare(F64, Gt, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    else -> unknown(this)
}

private fun NumericInstruction.form22(): NumericForm = when (val i = this) {
    is NumericInstruction.F64GtSi -> compare(F64, Gt, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64GtSs -> compare(F64, Gt, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64LeIi -> compare(F64, Le, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64LeIs -> compare(F64, Le, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64LeSi -> compare(F64, Le, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64LeSs -> compare(F64, Le, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64GeIi -> compare(F64, Ge, immediate(i.left), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64GeIs -> compare(F64, Ge, immediate(i.left), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64GeSi -> compare(F64, Ge, slot(i.leftSlot), immediate(i.right), i.destinationSlot)
    is NumericInstruction.F64GeSs -> compare(F64, Ge, slot(i.leftSlot), slot(i.rightSlot), i.destinationSlot)
    is NumericInstruction.F64ConvertI32SI -> convert(F64ConvertI32S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64ConvertI32SS -> convert(F64ConvertI32S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64ConvertI32UI -> convert(F64ConvertI32U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64ConvertI32US -> convert(F64ConvertI32U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64ConvertI64SI -> convert(F64ConvertI64S, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64ConvertI64SS -> convert(F64ConvertI64S, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64ConvertI64UI -> convert(F64ConvertI64U, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64ConvertI64US -> convert(F64ConvertI64U, slot(i.operandSlot), i.destinationSlot)
    is NumericInstruction.F64PromoteF32I -> convert(F64PromoteF32, immediate(i.operand), i.destinationSlot)
    is NumericInstruction.F64PromoteF32S -> convert(F64PromoteF32, slot(i.operandSlot), i.destinationSlot)
    else -> unknown(this)
}

private val ZERO: Operand = Operand.Immediate(0)

private fun unary(
    type: NumberType,
    operation: UnaryOperation,
    operand: Operand,
    destination: Int,
) =
    NumericForm.Unary(type, operation, operand, destination)

private fun binary(
    type: NumberType,
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) =
    NumericForm.Binary(type, operation, left, right, destination)

private fun compare(
    type: NumberType,
    comparison: Comparison,
    left: Operand,
    right: Operand,
    destination: Int,
) =
    NumericForm.Compare(type, comparison, left, right, destination)

private fun convert(
    conversion: Conversion,
    operand: Operand,
    destination: Int,
) = NumericForm.Convert(conversion, operand, destination)

private fun wide(
    operation: WideOperation,
    left: Operand,
    right: Operand,
    low: Int,
    high: Int,
) =
    NumericForm.Wide(operation, listOf(left, right), low, high)

private fun wide(
    operation: WideOperation,
    leftLow: Operand,
    leftHigh: Operand,
    rightLow: Operand,
    rightHigh: Operand,
    low: Int,
    high: Int,
) = NumericForm.Wide(operation, listOf(leftLow, leftHigh, rightLow, rightHigh), low, high)
