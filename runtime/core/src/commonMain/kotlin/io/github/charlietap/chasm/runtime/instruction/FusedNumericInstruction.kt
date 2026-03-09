package io.github.charlietap.chasm.runtime.instruction

sealed interface FusedNumericInstruction : LinkedInstruction {

    data class I32ConstS(
        val value: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ConstS(
        val value: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConstS(
        val bits: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConstS(
        val bits: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AddIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AddIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AddSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32SubIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32SubIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32SubSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32MulIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32MulIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32MulSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32DivUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RemUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AndIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AndIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AndSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32AndSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32OrIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32OrIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32OrSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32OrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32XorIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32XorIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32XorSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32XorSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShlIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShlIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShlSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ShrUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotlIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotlIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotlSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotrIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotrIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotrSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32RotrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AddIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AddIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AddSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64SubIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64SubIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64SubSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Iiii(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Iiis(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Iisi(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Iiss(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Isii(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Isis(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Issi(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Isss(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Siii(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Siis(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Sisi(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Siss(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Ssii(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Ssis(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Sssi(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Add128Ssss(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Iiii(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Iiis(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Iisi(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Iiss(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Isii(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Isis(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Issi(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Isss(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Siii(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Siis(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Sisi(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Siss(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Ssii(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Ssis(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Sssi(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64Sub128Ssss(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideSIi(
        val left: Long,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideUIi(
        val left: Long,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64MulWideUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64DivUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RemUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AndIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AndIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AndSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64AndSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64OrIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64OrIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64OrSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64OrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64XorIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64XorIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64XorSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64XorSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShlIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShlIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShlSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ShrUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotlIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotlIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotlSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotrIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotrIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotrSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64RotrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ClzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ClzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32CtzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32CtzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32PopcntI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32PopcntS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ClzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ClzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64CtzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64CtzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64PopcntI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64PopcntS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32NeIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32NeIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32NeSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32LeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32GeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64NeIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64NeIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64NeSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64LeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64GeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32Extend16SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32Extend16SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32Extend8SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32Extend8SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ReinterpretF32I(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32ReinterpretF32S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF32SI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF32UI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF64SI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF64UI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF32SI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF32UI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF64SI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF64UI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32TruncSatF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32WrapI64I(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I32WrapI64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend16SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend16SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend32SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend8SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64Extend8SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ExtendI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ExtendI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ExtendI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ExtendI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ReinterpretF64I(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64ReinterpretF64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF32SI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF32UI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF64SI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF64UI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF32SI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF32UI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF64SI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF64UI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class I64TruncSatF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AddIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AddIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AddSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SubIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SubIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SubSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MulIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MulIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MulSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DivIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DivIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DivSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DivSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MinIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MinIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MinSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MinSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MaxIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MaxIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MaxSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32MaxSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CopysignIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CopysignIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CopysignSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CopysignSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AbsI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32AbsS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NegI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NegS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CeilI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32CeilS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32FloorI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32FloorS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32TruncI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32TruncS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NearestI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NearestS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SqrtI(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32SqrtS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32EqIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32EqIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32EqSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NeIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NeIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NeSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LtIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LtIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LtSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GtIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GtIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GtSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LeIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LeIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LeSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32LeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GeIi(
        val left: Float,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GeIs(
        val left: Float,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GeSi(
        val leftSlot: Int,
        val right: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32GeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI64SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI64UI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ConvertI64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DemoteF64I(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32DemoteF64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ReinterpretI32I(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F32ReinterpretI32S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AddIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AddIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AddSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SubIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SubIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SubSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MulIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MulIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MulSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64DivIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64DivIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64DivSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64DivSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MinIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MinIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MinSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MinSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MaxIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MaxIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MaxSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64MaxSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CopysignIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CopysignIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CopysignSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CopysignSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AbsI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64AbsS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NegI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NegS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CeilI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64CeilS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64FloorI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64FloorS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64TruncI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64TruncS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NearestI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NearestS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SqrtI(
        val operand: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64SqrtS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64EqIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64EqIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64EqSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NeIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NeIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NeSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LtIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LtIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LtSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GtIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GtIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GtSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LeIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LeIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LeSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64LeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GeIi(
        val left: Double,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GeIs(
        val left: Double,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GeSi(
        val leftSlot: Int,
        val right: Double,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64GeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI64SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI64UI(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ConvertI64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64PromoteF32I(
        val operand: Float,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64PromoteF32S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ReinterpretI64I(
        val operand: Long,
        val destinationSlot: Int,
    ) : FusedNumericInstruction

    data class F64ReinterpretI64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : FusedNumericInstruction
}
