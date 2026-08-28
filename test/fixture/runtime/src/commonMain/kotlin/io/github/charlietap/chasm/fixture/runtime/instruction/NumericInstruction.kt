package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun numericRuntimeInstruction(): NumericInstruction = i32ConstSRuntimeInstruction()

fun i32ConstSRuntimeInstruction(
    value: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ConstS(
    value = value,
    destinationSlot = destinationSlot,
)

fun i32BitFieldExtractSRuntimeInstruction(
    operandSlot: Int = 0,
    shift: Int = 0,
    mask: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32BitFieldExtractS(
    operandSlot = operandSlot,
    shift = shift,
    mask = mask,
    destinationSlot = destinationSlot,
)

fun i64ConstSRuntimeInstruction(
    value: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ConstS(
    value = value,
    destinationSlot = destinationSlot,
)

fun f32ConstSRuntimeInstruction(
    bits: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConstS(
    bits = bits,
    destinationSlot = destinationSlot,
)

fun f64ConstSRuntimeInstruction(
    bits: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConstS(
    bits = bits,
    destinationSlot = destinationSlot,
)

fun i32AddIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AddIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32AddSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AddSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32AddSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AddSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32SubIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32SubIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32SubIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32SubIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32SubSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32SubSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32SubSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32SubSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32MulIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32MulIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32MulIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32MulIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32MulSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32MulSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32MulSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32MulSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32DivSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32DivSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32DivSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32DivSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32DivUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32DivUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32DivUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32DivUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32DivUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RemSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RemSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RemSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RemSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RemUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RemUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RemUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RemUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RemUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32AndIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AndIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32AndIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AndIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32AndSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AndSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32AndSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32AndSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32OrIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32OrIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32OrIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32OrIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32OrSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32OrSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32OrSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32OrSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32XorIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32XorIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32XorIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32XorIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32XorSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32XorSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32XorSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32XorSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShlIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShlIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShlIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShlIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShlSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShlSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShlSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShlSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShrSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShrSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShrSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShrSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShrUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShrUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32ShrUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32ShrUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ShrUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RotlIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotlIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RotlIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotlIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RotlSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotlSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RotlSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotlSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RotrIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotrIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RotrIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotrIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32RotrSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotrSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32RotrSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32RotrSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64AddIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AddIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64AddIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AddIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64AddSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AddSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64AddSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AddSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64SubIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64SubIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64SubIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64SubIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64SubSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64SubSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64SubSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64SubSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64MulIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64MulIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64MulIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64MulIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64MulSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64MulSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64MulSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64MulSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64Add128IiiiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Iiii(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IiisRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Iiis(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IisiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Iisi(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IissRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Iiss(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IsiiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Isii(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IsisRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Isis(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IssiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Issi(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128IsssRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Isss(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SiiiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Siii(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SiisRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Siis(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SisiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Sisi(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SissRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Siss(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SsiiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Ssii(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SsisRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Ssis(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SssiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Sssi(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Add128SsssRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Add128Ssss(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IiiiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Iiii(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IiisRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Iiis(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IisiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Iisi(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IissRuntimeInstruction(
    leftLow: Long = 0L,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Iiss(
    leftLow = leftLow,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IsiiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Isii(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IsisRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Isis(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IssiRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Issi(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128IsssRuntimeInstruction(
    leftLow: Long = 0L,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Isss(
    leftLow = leftLow,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SiiiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Siii(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SiisRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Siis(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SisiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Sisi(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SissRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHigh: Long = 0L,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Siss(
    leftLowSlot = leftLowSlot,
    leftHigh = leftHigh,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SsiiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Ssii(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SsisRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLow: Long = 0L,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Ssis(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLow = rightLow,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SssiRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHigh: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Sssi(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHigh = rightHigh,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64Sub128SsssRuntimeInstruction(
    leftLowSlot: Int = 0,
    leftHighSlot: Int = 0,
    rightLowSlot: Int = 0,
    rightHighSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64Sub128Ssss(
    leftLowSlot = leftLowSlot,
    leftHighSlot = leftHighSlot,
    rightLowSlot = rightLowSlot,
    rightHighSlot = rightHighSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideSIi(
    left = left,
    right = right,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideSIs(
    left = left,
    rightSlot = rightSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideSSi(
    leftSlot = leftSlot,
    right = right,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideUIi(
    left = left,
    right = right,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideUIs(
    left = left,
    rightSlot = rightSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideUSi(
    leftSlot = leftSlot,
    right = right,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64MulWideUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationLowSlot: Int = 0,
    destinationHighSlot: Int = 0,
) = NumericInstruction.I64MulWideUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationLowSlot = destinationLowSlot,
    destinationHighSlot = destinationHighSlot,
)

fun i64DivSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64DivSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64DivSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64DivSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64DivUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64DivUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64DivUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64DivUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64DivUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RemSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RemSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RemSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RemSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RemUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RemUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RemUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RemUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RemUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64AndIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AndIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64AndIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AndIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64AndSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AndSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64AndSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64AndSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64OrIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64OrIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64OrIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64OrIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64OrSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64OrSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64OrSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64OrSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64XorIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64XorIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64XorIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64XorIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64XorSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64XorSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64XorSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64XorSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShlIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShlIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShlIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShlIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShlSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShlSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShlSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShlSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShrSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShrSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShrSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShrSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShrUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShrUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64ShrUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64ShrUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ShrUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RotlIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotlIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RotlIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotlIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RotlSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotlSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RotlSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotlSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RotrIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotrIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RotrIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotrIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64RotrSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotrSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64RotrSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64RotrSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32EqzIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32EqzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64EqzIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64EqzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32ClzIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ClzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32ClzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32ClzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32CtzIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32CtzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32CtzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32CtzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32PopcntIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32PopcntI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32PopcntSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32PopcntS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64ClzIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ClzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64ClzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ClzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64CtzIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64CtzI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64CtzSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64CtzS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64PopcntIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64PopcntI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64PopcntSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64PopcntS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32EqIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32EqIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32EqSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32EqSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32EqSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32NeIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32NeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32NeIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32NeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32NeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32NeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32NeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32NeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LtSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LtSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LtSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LtSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LtUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LtUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LtUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LtUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LtUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GtSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GtSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GtSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GtSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GtUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GtUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GtUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GtUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GtUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LeSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LeSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LeSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LeSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LeUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LeUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32LeUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32LeUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32LeUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GeSIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GeSIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GeSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GeSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GeUIiRuntimeInstruction(
    left: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GeUIsRuntimeInstruction(
    left: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32GeUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i32GeUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32GeUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64EqIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64EqIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64EqSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64EqSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64EqSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64NeIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64NeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64NeIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64NeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64NeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64NeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64NeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64NeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LtSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LtSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LtSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LtSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LtUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LtUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LtUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LtUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LtUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GtSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GtSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GtSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GtSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GtUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GtUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GtUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GtUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GtUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LeSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LeSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LeSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LeSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LeUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LeUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64LeUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64LeUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64LeUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GeSIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeSIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GeSIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeSIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GeSSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeSSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GeSSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeSSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GeUIiRuntimeInstruction(
    left: Long = 0L,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeUIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GeUIsRuntimeInstruction(
    left: Long = 0L,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeUIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i64GeUSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeUSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun i64GeUSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64GeUSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun i32Extend16SIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32Extend16SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32Extend16SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32Extend16SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32Extend8SIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32Extend8SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32Extend8SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32Extend8SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncF32SIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncF32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncF32UIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncF32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncF64SIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncF64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncF64UIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncF64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncF64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF32SIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF32UIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF64SIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF64UIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32TruncSatF64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32TruncSatF64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i32WrapI64IRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I32WrapI64I(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i32WrapI64SRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I32WrapI64S(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64Extend16SIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend16SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64Extend16SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend16SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64Extend32SIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64Extend32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64Extend8SIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend8SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64Extend8SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64Extend8SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64ExtendI32SIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ExtendI32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64ExtendI32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ExtendI32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64ExtendI32UIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ExtendI32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64ExtendI32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64ExtendI32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncF32SIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncF32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncF32UIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncF32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncF64SIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncF64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncF64UIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncF64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncF64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF32SIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF32UIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF64SIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF64UIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun i64TruncSatF64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.I64TruncSatF64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32AddIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AddIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32AddIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AddIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32AddSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AddSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32AddSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AddSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32SubIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SubIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32SubIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SubIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32SubSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SubSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32SubSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SubSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MulIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MulIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MulIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MulIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MulSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MulSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MulSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MulSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32DivIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DivIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32DivIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DivIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32DivSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DivSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32DivSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DivSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MinIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MinIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MinIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MinIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MinSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MinSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MinSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MinSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MaxIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MaxIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MaxIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MaxIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32MaxSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MaxSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32MaxSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32MaxSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32CopysignIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CopysignIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32CopysignIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CopysignIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32CopysignSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CopysignSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32CopysignSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CopysignSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32AbsIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AbsI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32AbsSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32AbsS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32NegIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NegI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32NegSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NegS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32CeilIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CeilI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32CeilSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32CeilS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32FloorIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32FloorI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32FloorSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32FloorS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32TruncIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32TruncI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32TruncSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32TruncS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32NearestIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NearestI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32NearestSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NearestS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32SqrtIRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SqrtI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32SqrtSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32SqrtS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32EqIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32EqIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32EqIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32EqIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32EqSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32EqSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32EqSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32EqSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32NeIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32NeIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32NeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32NeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32NeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32LtIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LtIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32LtIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LtIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32LtSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LtSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32LtSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LtSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32GtIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GtIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32GtIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GtIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32GtSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GtSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32GtSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GtSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32LeIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32LeIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32LeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32LeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32LeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32GeIiRuntimeInstruction(
    left: Float = 0f,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32GeIsRuntimeInstruction(
    left: Float = 0f,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32GeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f32GeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32GeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f32ConvertI32SIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32ConvertI32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32ConvertI32UIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32ConvertI32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32ConvertI64SIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32ConvertI64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32ConvertI64UIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32ConvertI64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32ConvertI64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f32DemoteF64IRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DemoteF64I(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f32DemoteF64SRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F32DemoteF64S(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64AddIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AddIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64AddIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AddIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64AddSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AddSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64AddSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AddSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64SubIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SubIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64SubIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SubIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64SubSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SubSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64SubSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SubSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MulIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MulIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MulIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MulIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MulSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MulSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MulSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MulSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64DivIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64DivIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64DivIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64DivIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64DivSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64DivSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64DivSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64DivSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MinIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MinIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MinIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MinIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MinSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MinSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MinSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MinSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MaxIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MaxIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MaxIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MaxIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64MaxSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MaxSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64MaxSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64MaxSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64CopysignIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CopysignIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64CopysignIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CopysignIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64CopysignSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CopysignSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64CopysignSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CopysignSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64AbsIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AbsI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64AbsSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64AbsS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64NegIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NegI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64NegSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NegS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64CeilIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CeilI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64CeilSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64CeilS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64FloorIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64FloorI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64FloorSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64FloorS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64TruncIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64TruncI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64TruncSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64TruncS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64NearestIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NearestI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64NearestSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NearestS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64SqrtIRuntimeInstruction(
    operand: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SqrtI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64SqrtSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64SqrtS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64EqIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64EqIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64EqIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64EqIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64EqSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64EqSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64EqSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64EqSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64NeIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64NeIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64NeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64NeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64NeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64LtIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LtIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64LtIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LtIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64LtSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LtSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64LtSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LtSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64GtIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GtIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64GtIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GtIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64GtSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GtSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64GtSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GtSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64LeIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64LeIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64LeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64LeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64LeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64GeIiRuntimeInstruction(
    left: Double = 0.0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GeIi(
    left = left,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64GeIsRuntimeInstruction(
    left: Double = 0.0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GeIs(
    left = left,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64GeSiRuntimeInstruction(
    leftSlot: Int = 0,
    right: Double = 0.0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GeSi(
    leftSlot = leftSlot,
    right = right,
    destinationSlot = destinationSlot,
)

fun f64GeSsRuntimeInstruction(
    leftSlot: Int = 0,
    rightSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64GeSs(
    leftSlot = leftSlot,
    rightSlot = rightSlot,
    destinationSlot = destinationSlot,
)

fun f64ConvertI32SIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI32SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64ConvertI32SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI32SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64ConvertI32UIRuntimeInstruction(
    operand: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI32UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64ConvertI32USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI32US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64ConvertI64SIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI64SI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64ConvertI64SSRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI64SS(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64ConvertI64UIRuntimeInstruction(
    operand: Long = 0L,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI64UI(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64ConvertI64USRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64ConvertI64US(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)

fun f64PromoteF32IRuntimeInstruction(
    operand: Float = 0f,
    destinationSlot: Int = 0,
) = NumericInstruction.F64PromoteF32I(
    operand = operand,
    destinationSlot = destinationSlot,
)

fun f64PromoteF32SRuntimeInstruction(
    operandSlot: Int = 0,
    destinationSlot: Int = 0,
) = NumericInstruction.F64PromoteF32S(
    operandSlot = operandSlot,
    destinationSlot = destinationSlot,
)
