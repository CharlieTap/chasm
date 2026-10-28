package io.github.charlietap.chasm.cir

sealed class NumericInstruction : Instruction() {

    @CirTag("numeric.i32.const")
    data class I32ConstS(
        val value: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 0
        }
    }

    @CirTag("numeric.i32.bit_field_extract.s")
    data class I32BitFieldExtractS(
        val operandSlot: Int,
        val shift: Int,
        val mask: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 1
        }
    }

    @CirTag("numeric.i64.const")
    data class I64ConstS(
        val value: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 2
        }
    }

    @CirTag("numeric.f32.const")
    data class F32ConstS(
        val bits: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 3
        }
    }

    @CirTag("numeric.f64.const")
    data class F64ConstS(
        val bits: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 4
        }
    }

    @CirTag("numeric.i32.add.is")
    data class I32AddIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 5
        }
    }

    @CirTag("numeric.i32.add.si")
    data class I32AddSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 6
        }
    }

    @CirTag("numeric.i32.add.ss")
    data class I32AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 7
        }
    }

    @CirTag("numeric.i32.sub.ii")
    data class I32SubIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 8
        }
    }

    @CirTag("numeric.i32.sub.is")
    data class I32SubIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 9
        }
    }

    @CirTag("numeric.i32.sub.si")
    data class I32SubSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 10
        }
    }

    @CirTag("numeric.i32.sub.ss")
    data class I32SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 11
        }
    }

    @CirTag("numeric.i32.mul.ii")
    data class I32MulIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 12
        }
    }

    @CirTag("numeric.i32.mul.is")
    data class I32MulIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 13
        }
    }

    @CirTag("numeric.i32.mul.si")
    data class I32MulSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 14
        }
    }

    @CirTag("numeric.i32.mul.ss")
    data class I32MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 15
        }
    }

    @CirTag("numeric.i32.div_s.ii")
    data class I32DivSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 16
        }
    }

    @CirTag("numeric.i32.div_s.is")
    data class I32DivSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 17
        }
    }

    @CirTag("numeric.i32.div_s.si")
    data class I32DivSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 18
        }
    }

    @CirTag("numeric.i32.div_s.ss")
    data class I32DivSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 19
        }
    }

    @CirTag("numeric.i32.div_u.ii")
    data class I32DivUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 20
        }
    }

    @CirTag("numeric.i32.div_u.is")
    data class I32DivUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 21
        }
    }

    @CirTag("numeric.i32.div_u.si")
    data class I32DivUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 22
        }
    }

    @CirTag("numeric.i32.div_u.ss")
    data class I32DivUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 23
        }
    }

    @CirTag("numeric.i32.rem_s.ii")
    data class I32RemSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 24
        }
    }

    @CirTag("numeric.i32.rem_s.is")
    data class I32RemSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 25
        }
    }

    @CirTag("numeric.i32.rem_s.si")
    data class I32RemSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 26
        }
    }

    @CirTag("numeric.i32.rem_s.ss")
    data class I32RemSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 27
        }
    }

    @CirTag("numeric.i32.rem_u.ii")
    data class I32RemUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 28
        }
    }

    @CirTag("numeric.i32.rem_u.is")
    data class I32RemUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 29
        }
    }

    @CirTag("numeric.i32.rem_u.si")
    data class I32RemUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 30
        }
    }

    @CirTag("numeric.i32.rem_u.ss")
    data class I32RemUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 31
        }
    }

    @CirTag("numeric.i32.and.ii")
    data class I32AndIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 32
        }
    }

    @CirTag("numeric.i32.and.is")
    data class I32AndIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 33
        }
    }

    @CirTag("numeric.i32.and.si")
    data class I32AndSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 34
        }
    }

    @CirTag("numeric.i32.and.ss")
    data class I32AndSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 35
        }
    }

    @CirTag("numeric.i32.or.ii")
    data class I32OrIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 36
        }
    }

    @CirTag("numeric.i32.or.is")
    data class I32OrIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 37
        }
    }

    @CirTag("numeric.i32.or.si")
    data class I32OrSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 38
        }
    }

    @CirTag("numeric.i32.or.ss")
    data class I32OrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 39
        }
    }

    @CirTag("numeric.i32.xor.ii")
    data class I32XorIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 40
        }
    }

    @CirTag("numeric.i32.xor.is")
    data class I32XorIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 41
        }
    }

    @CirTag("numeric.i32.xor.si")
    data class I32XorSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 42
        }
    }

    @CirTag("numeric.i32.xor.ss")
    data class I32XorSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 43
        }
    }

    @CirTag("numeric.i32.shl.ii")
    data class I32ShlIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 44
        }
    }

    @CirTag("numeric.i32.shl.is")
    data class I32ShlIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 45
        }
    }

    @CirTag("numeric.i32.shl.si")
    data class I32ShlSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 46
        }
    }

    @CirTag("numeric.i32.shl.ss")
    data class I32ShlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 47
        }
    }

    @CirTag("numeric.i32.shr_s.ii")
    data class I32ShrSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 48
        }
    }

    @CirTag("numeric.i32.shr_s.is")
    data class I32ShrSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 49
        }
    }

    @CirTag("numeric.i32.shr_s.si")
    data class I32ShrSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 50
        }
    }

    @CirTag("numeric.i32.shr_s.ss")
    data class I32ShrSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 51
        }
    }

    @CirTag("numeric.i32.shr_u.ii")
    data class I32ShrUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 52
        }
    }

    @CirTag("numeric.i32.shr_u.is")
    data class I32ShrUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 53
        }
    }

    @CirTag("numeric.i32.shr_u.si")
    data class I32ShrUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 54
        }
    }

    @CirTag("numeric.i32.shr_u.ss")
    data class I32ShrUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 55
        }
    }

    @CirTag("numeric.i32.rotl.ii")
    data class I32RotlIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 56
        }
    }

    @CirTag("numeric.i32.rotl.is")
    data class I32RotlIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 57
        }
    }

    @CirTag("numeric.i32.rotl.si")
    data class I32RotlSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 58
        }
    }

    @CirTag("numeric.i32.rotl.ss")
    data class I32RotlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 59
        }
    }

    @CirTag("numeric.i32.rotr.ii")
    data class I32RotrIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 60
        }
    }

    @CirTag("numeric.i32.rotr.is")
    data class I32RotrIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 61
        }
    }

    @CirTag("numeric.i32.rotr.si")
    data class I32RotrSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 62
        }
    }

    @CirTag("numeric.i32.rotr.ss")
    data class I32RotrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 63
        }
    }

    @CirTag("numeric.i64.add.ii")
    data class I64AddIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 64
        }
    }

    @CirTag("numeric.i64.add.is")
    data class I64AddIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 65
        }
    }

    @CirTag("numeric.i64.add.si")
    data class I64AddSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 66
        }
    }

    @CirTag("numeric.i64.add.ss")
    data class I64AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 67
        }
    }

    @CirTag("numeric.i64.sub.ii")
    data class I64SubIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 68
        }
    }

    @CirTag("numeric.i64.sub.is")
    data class I64SubIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 69
        }
    }

    @CirTag("numeric.i64.sub.si")
    data class I64SubSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 70
        }
    }

    @CirTag("numeric.i64.sub.ss")
    data class I64SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 71
        }
    }

    @CirTag("numeric.i64.mul.ii")
    data class I64MulIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 72
        }
    }

    @CirTag("numeric.i64.mul.is")
    data class I64MulIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 73
        }
    }

    @CirTag("numeric.i64.mul.si")
    data class I64MulSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 74
        }
    }

    @CirTag("numeric.i64.mul.ss")
    data class I64MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 75
        }
    }

    @CirTag("numeric.i64.add128.iiii")
    data class I64Add128Iiii(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 76
        }
    }

    @CirTag("numeric.i64.add128.iiis")
    data class I64Add128Iiis(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 77
        }
    }

    @CirTag("numeric.i64.add128.iisi")
    data class I64Add128Iisi(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 78
        }
    }

    @CirTag("numeric.i64.add128.iiss")
    data class I64Add128Iiss(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 79
        }
    }

    @CirTag("numeric.i64.add128.isii")
    data class I64Add128Isii(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 80
        }
    }

    @CirTag("numeric.i64.add128.isis")
    data class I64Add128Isis(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 81
        }
    }

    @CirTag("numeric.i64.add128.issi")
    data class I64Add128Issi(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 82
        }
    }

    @CirTag("numeric.i64.add128.isss")
    data class I64Add128Isss(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 83
        }
    }

    @CirTag("numeric.i64.add128.siii")
    data class I64Add128Siii(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 84
        }
    }

    @CirTag("numeric.i64.add128.siis")
    data class I64Add128Siis(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 85
        }
    }

    @CirTag("numeric.i64.add128.sisi")
    data class I64Add128Sisi(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 86
        }
    }

    @CirTag("numeric.i64.add128.siss")
    data class I64Add128Siss(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 87
        }
    }

    @CirTag("numeric.i64.add128.ssii")
    data class I64Add128Ssii(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 88
        }
    }

    @CirTag("numeric.i64.add128.ssis")
    data class I64Add128Ssis(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 89
        }
    }

    @CirTag("numeric.i64.add128.sssi")
    data class I64Add128Sssi(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 90
        }
    }

    @CirTag("numeric.i64.add128.ssss")
    data class I64Add128Ssss(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 91
        }
    }

    @CirTag("numeric.i64.sub128.iiii")
    data class I64Sub128Iiii(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 92
        }
    }

    @CirTag("numeric.i64.sub128.iiis")
    data class I64Sub128Iiis(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 93
        }
    }

    @CirTag("numeric.i64.sub128.iisi")
    data class I64Sub128Iisi(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 94
        }
    }

    @CirTag("numeric.i64.sub128.iiss")
    data class I64Sub128Iiss(
        val leftLow: Long,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 95
        }
    }

    @CirTag("numeric.i64.sub128.isii")
    data class I64Sub128Isii(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 96
        }
    }

    @CirTag("numeric.i64.sub128.isis")
    data class I64Sub128Isis(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 97
        }
    }

    @CirTag("numeric.i64.sub128.issi")
    data class I64Sub128Issi(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 98
        }
    }

    @CirTag("numeric.i64.sub128.isss")
    data class I64Sub128Isss(
        val leftLow: Long,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 99
        }
    }

    @CirTag("numeric.i64.sub128.siii")
    data class I64Sub128Siii(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 100
        }
    }

    @CirTag("numeric.i64.sub128.siis")
    data class I64Sub128Siis(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 101
        }
    }

    @CirTag("numeric.i64.sub128.sisi")
    data class I64Sub128Sisi(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 102
        }
    }

    @CirTag("numeric.i64.sub128.siss")
    data class I64Sub128Siss(
        val leftLowSlot: Int,
        val leftHigh: Long,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 103
        }
    }

    @CirTag("numeric.i64.sub128.ssii")
    data class I64Sub128Ssii(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 104
        }
    }

    @CirTag("numeric.i64.sub128.ssis")
    data class I64Sub128Ssis(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLow: Long,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 105
        }
    }

    @CirTag("numeric.i64.sub128.sssi")
    data class I64Sub128Sssi(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHigh: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 106
        }
    }

    @CirTag("numeric.i64.sub128.ssss")
    data class I64Sub128Ssss(
        val leftLowSlot: Int,
        val leftHighSlot: Int,
        val rightLowSlot: Int,
        val rightHighSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 107
        }
    }

    @CirTag("numeric.i64.mul_wide_s.ii")
    data class I64MulWideSIi(
        val left: Long,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 108
        }
    }

    @CirTag("numeric.i64.mul_wide_s.is")
    data class I64MulWideSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 109
        }
    }

    @CirTag("numeric.i64.mul_wide_s.si")
    data class I64MulWideSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 110
        }
    }

    @CirTag("numeric.i64.mul_wide_s.ss")
    data class I64MulWideSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 111
        }
    }

    @CirTag("numeric.i64.mul_wide_u.ii")
    data class I64MulWideUIi(
        val left: Long,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 112
        }
    }

    @CirTag("numeric.i64.mul_wide_u.is")
    data class I64MulWideUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 113
        }
    }

    @CirTag("numeric.i64.mul_wide_u.si")
    data class I64MulWideUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 114
        }
    }

    @CirTag("numeric.i64.mul_wide_u.ss")
    data class I64MulWideUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationLowSlot: Int,
        val destinationHighSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 115
        }
    }

    @CirTag("numeric.i64.div_s.ii")
    data class I64DivSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 116
        }
    }

    @CirTag("numeric.i64.div_s.is")
    data class I64DivSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 117
        }
    }

    @CirTag("numeric.i64.div_s.si")
    data class I64DivSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 118
        }
    }

    @CirTag("numeric.i64.div_s.ss")
    data class I64DivSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 119
        }
    }

    @CirTag("numeric.i64.div_u.ii")
    data class I64DivUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 120
        }
    }

    @CirTag("numeric.i64.div_u.is")
    data class I64DivUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 121
        }
    }

    @CirTag("numeric.i64.div_u.si")
    data class I64DivUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 122
        }
    }

    @CirTag("numeric.i64.div_u.ss")
    data class I64DivUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 123
        }
    }

    @CirTag("numeric.i64.rem_s.ii")
    data class I64RemSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 124
        }
    }

    @CirTag("numeric.i64.rem_s.is")
    data class I64RemSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 125
        }
    }

    @CirTag("numeric.i64.rem_s.si")
    data class I64RemSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 126
        }
    }

    @CirTag("numeric.i64.rem_s.ss")
    data class I64RemSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 127
        }
    }

    @CirTag("numeric.i64.rem_u.ii")
    data class I64RemUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 128
        }
    }

    @CirTag("numeric.i64.rem_u.is")
    data class I64RemUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 129
        }
    }

    @CirTag("numeric.i64.rem_u.si")
    data class I64RemUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 130
        }
    }

    @CirTag("numeric.i64.rem_u.ss")
    data class I64RemUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 131
        }
    }

    @CirTag("numeric.i64.and.ii")
    data class I64AndIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 132
        }
    }

    @CirTag("numeric.i64.and.is")
    data class I64AndIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 133
        }
    }

    @CirTag("numeric.i64.and.si")
    data class I64AndSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 134
        }
    }

    @CirTag("numeric.i64.and.ss")
    data class I64AndSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 135
        }
    }

    @CirTag("numeric.i64.or.ii")
    data class I64OrIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 136
        }
    }

    @CirTag("numeric.i64.or.is")
    data class I64OrIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 137
        }
    }

    @CirTag("numeric.i64.or.si")
    data class I64OrSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 138
        }
    }

    @CirTag("numeric.i64.or.ss")
    data class I64OrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 139
        }
    }

    @CirTag("numeric.i64.xor.ii")
    data class I64XorIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 140
        }
    }

    @CirTag("numeric.i64.xor.is")
    data class I64XorIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 141
        }
    }

    @CirTag("numeric.i64.xor.si")
    data class I64XorSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 142
        }
    }

    @CirTag("numeric.i64.xor.ss")
    data class I64XorSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 143
        }
    }

    @CirTag("numeric.i64.shl.ii")
    data class I64ShlIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 144
        }
    }

    @CirTag("numeric.i64.shl.is")
    data class I64ShlIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 145
        }
    }

    @CirTag("numeric.i64.shl.si")
    data class I64ShlSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 146
        }
    }

    @CirTag("numeric.i64.shl.ss")
    data class I64ShlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 147
        }
    }

    @CirTag("numeric.i64.shr_s.ii")
    data class I64ShrSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 148
        }
    }

    @CirTag("numeric.i64.shr_s.is")
    data class I64ShrSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 149
        }
    }

    @CirTag("numeric.i64.shr_s.si")
    data class I64ShrSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 150
        }
    }

    @CirTag("numeric.i64.shr_s.ss")
    data class I64ShrSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 151
        }
    }

    @CirTag("numeric.i64.shr_u.ii")
    data class I64ShrUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 152
        }
    }

    @CirTag("numeric.i64.shr_u.is")
    data class I64ShrUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 153
        }
    }

    @CirTag("numeric.i64.shr_u.si")
    data class I64ShrUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 154
        }
    }

    @CirTag("numeric.i64.shr_u.ss")
    data class I64ShrUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 155
        }
    }

    @CirTag("numeric.i64.rotl.ii")
    data class I64RotlIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 156
        }
    }

    @CirTag("numeric.i64.rotl.is")
    data class I64RotlIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 157
        }
    }

    @CirTag("numeric.i64.rotl.si")
    data class I64RotlSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 158
        }
    }

    @CirTag("numeric.i64.rotl.ss")
    data class I64RotlSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 159
        }
    }

    @CirTag("numeric.i64.rotr.ii")
    data class I64RotrIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 160
        }
    }

    @CirTag("numeric.i64.rotr.is")
    data class I64RotrIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 161
        }
    }

    @CirTag("numeric.i64.rotr.si")
    data class I64RotrSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 162
        }
    }

    @CirTag("numeric.i64.rotr.ss")
    data class I64RotrSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 163
        }
    }

    @CirTag("numeric.i32.eqz.i")
    data class I32EqzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 164
        }
    }

    @CirTag("numeric.i32.eqz.s")
    data class I32EqzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 165
        }
    }

    @CirTag("numeric.i64.eqz.i")
    data class I64EqzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 166
        }
    }

    @CirTag("numeric.i64.eqz.s")
    data class I64EqzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 167
        }
    }

    @CirTag("numeric.i32.clz.i")
    data class I32ClzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 168
        }
    }

    @CirTag("numeric.i32.clz.s")
    data class I32ClzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 169
        }
    }

    @CirTag("numeric.i32.ctz.i")
    data class I32CtzI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 170
        }
    }

    @CirTag("numeric.i32.ctz.s")
    data class I32CtzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 171
        }
    }

    @CirTag("numeric.i32.popcnt.i")
    data class I32PopcntI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 172
        }
    }

    @CirTag("numeric.i32.popcnt.s")
    data class I32PopcntS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 173
        }
    }

    @CirTag("numeric.i64.clz.i")
    data class I64ClzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 174
        }
    }

    @CirTag("numeric.i64.clz.s")
    data class I64ClzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 175
        }
    }

    @CirTag("numeric.i64.ctz.i")
    data class I64CtzI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 176
        }
    }

    @CirTag("numeric.i64.ctz.s")
    data class I64CtzS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 177
        }
    }

    @CirTag("numeric.i64.popcnt.i")
    data class I64PopcntI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 178
        }
    }

    @CirTag("numeric.i64.popcnt.s")
    data class I64PopcntS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 179
        }
    }

    @CirTag("numeric.i32.eq.ii")
    data class I32EqIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 180
        }
    }

    @CirTag("numeric.i32.eq.is")
    data class I32EqIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 181
        }
    }

    @CirTag("numeric.i32.eq.si")
    data class I32EqSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 182
        }
    }

    @CirTag("numeric.i32.eq.ss")
    data class I32EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 183
        }
    }

    @CirTag("numeric.i32.ne.ii")
    data class I32NeIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 184
        }
    }

    @CirTag("numeric.i32.ne.is")
    data class I32NeIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 185
        }
    }

    @CirTag("numeric.i32.ne.si")
    data class I32NeSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 186
        }
    }

    @CirTag("numeric.i32.ne.ss")
    data class I32NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 187
        }
    }

    @CirTag("numeric.i32.lt_s.ii")
    data class I32LtSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 188
        }
    }

    @CirTag("numeric.i32.lt_s.is")
    data class I32LtSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 189
        }
    }

    @CirTag("numeric.i32.lt_s.si")
    data class I32LtSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 190
        }
    }

    @CirTag("numeric.i32.lt_s.ss")
    data class I32LtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 191
        }
    }

    @CirTag("numeric.i32.lt_u.ii")
    data class I32LtUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 192
        }
    }

    @CirTag("numeric.i32.lt_u.is")
    data class I32LtUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 193
        }
    }

    @CirTag("numeric.i32.lt_u.si")
    data class I32LtUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 194
        }
    }

    @CirTag("numeric.i32.lt_u.ss")
    data class I32LtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 195
        }
    }

    @CirTag("numeric.i32.gt_s.ii")
    data class I32GtSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 196
        }
    }

    @CirTag("numeric.i32.gt_s.is")
    data class I32GtSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 197
        }
    }

    @CirTag("numeric.i32.gt_s.si")
    data class I32GtSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 198
        }
    }

    @CirTag("numeric.i32.gt_s.ss")
    data class I32GtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 199
        }
    }

    @CirTag("numeric.i32.gt_u.ii")
    data class I32GtUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 200
        }
    }

    @CirTag("numeric.i32.gt_u.is")
    data class I32GtUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 201
        }
    }

    @CirTag("numeric.i32.gt_u.si")
    data class I32GtUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 202
        }
    }

    @CirTag("numeric.i32.gt_u.ss")
    data class I32GtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 203
        }
    }

    @CirTag("numeric.i32.le_s.ii")
    data class I32LeSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 204
        }
    }

    @CirTag("numeric.i32.le_s.is")
    data class I32LeSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 205
        }
    }

    @CirTag("numeric.i32.le_s.si")
    data class I32LeSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 206
        }
    }

    @CirTag("numeric.i32.le_s.ss")
    data class I32LeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 207
        }
    }

    @CirTag("numeric.i32.le_u.ii")
    data class I32LeUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 208
        }
    }

    @CirTag("numeric.i32.le_u.is")
    data class I32LeUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 209
        }
    }

    @CirTag("numeric.i32.le_u.si")
    data class I32LeUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 210
        }
    }

    @CirTag("numeric.i32.le_u.ss")
    data class I32LeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 211
        }
    }

    @CirTag("numeric.i32.ge_s.ii")
    data class I32GeSIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 212
        }
    }

    @CirTag("numeric.i32.ge_s.is")
    data class I32GeSIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 213
        }
    }

    @CirTag("numeric.i32.ge_s.si")
    data class I32GeSSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 214
        }
    }

    @CirTag("numeric.i32.ge_s.ss")
    data class I32GeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 215
        }
    }

    @CirTag("numeric.i32.ge_u.ii")
    data class I32GeUIi(
        val left: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 216
        }
    }

    @CirTag("numeric.i32.ge_u.is")
    data class I32GeUIs(
        val left: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 217
        }
    }

    @CirTag("numeric.i32.ge_u.si")
    data class I32GeUSi(
        val leftSlot: Int,
        val right: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 218
        }
    }

    @CirTag("numeric.i32.ge_u.ss")
    data class I32GeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 219
        }
    }

    @CirTag("numeric.i64.eq.ii")
    data class I64EqIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 220
        }
    }

    @CirTag("numeric.i64.eq.is")
    data class I64EqIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 221
        }
    }

    @CirTag("numeric.i64.eq.si")
    data class I64EqSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 222
        }
    }

    @CirTag("numeric.i64.eq.ss")
    data class I64EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 223
        }
    }

    @CirTag("numeric.i64.ne.ii")
    data class I64NeIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 224
        }
    }

    @CirTag("numeric.i64.ne.is")
    data class I64NeIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 225
        }
    }

    @CirTag("numeric.i64.ne.si")
    data class I64NeSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 226
        }
    }

    @CirTag("numeric.i64.ne.ss")
    data class I64NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 227
        }
    }

    @CirTag("numeric.i64.lt_s.ii")
    data class I64LtSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 228
        }
    }

    @CirTag("numeric.i64.lt_s.is")
    data class I64LtSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 229
        }
    }

    @CirTag("numeric.i64.lt_s.si")
    data class I64LtSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 230
        }
    }

    @CirTag("numeric.i64.lt_s.ss")
    data class I64LtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 231
        }
    }

    @CirTag("numeric.i64.lt_u.ii")
    data class I64LtUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 232
        }
    }

    @CirTag("numeric.i64.lt_u.is")
    data class I64LtUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 233
        }
    }

    @CirTag("numeric.i64.lt_u.si")
    data class I64LtUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 234
        }
    }

    @CirTag("numeric.i64.lt_u.ss")
    data class I64LtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 235
        }
    }

    @CirTag("numeric.i64.gt_s.ii")
    data class I64GtSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 236
        }
    }

    @CirTag("numeric.i64.gt_s.is")
    data class I64GtSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 237
        }
    }

    @CirTag("numeric.i64.gt_s.si")
    data class I64GtSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 238
        }
    }

    @CirTag("numeric.i64.gt_s.ss")
    data class I64GtSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 239
        }
    }

    @CirTag("numeric.i64.gt_u.ii")
    data class I64GtUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 240
        }
    }

    @CirTag("numeric.i64.gt_u.is")
    data class I64GtUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 241
        }
    }

    @CirTag("numeric.i64.gt_u.si")
    data class I64GtUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 242
        }
    }

    @CirTag("numeric.i64.gt_u.ss")
    data class I64GtUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 243
        }
    }

    @CirTag("numeric.i64.le_s.ii")
    data class I64LeSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 244
        }
    }

    @CirTag("numeric.i64.le_s.is")
    data class I64LeSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 245
        }
    }

    @CirTag("numeric.i64.le_s.si")
    data class I64LeSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 246
        }
    }

    @CirTag("numeric.i64.le_s.ss")
    data class I64LeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 247
        }
    }

    @CirTag("numeric.i64.le_u.ii")
    data class I64LeUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 248
        }
    }

    @CirTag("numeric.i64.le_u.is")
    data class I64LeUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 249
        }
    }

    @CirTag("numeric.i64.le_u.si")
    data class I64LeUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 250
        }
    }

    @CirTag("numeric.i64.le_u.ss")
    data class I64LeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 251
        }
    }

    @CirTag("numeric.i64.ge_s.ii")
    data class I64GeSIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 252
        }
    }

    @CirTag("numeric.i64.ge_s.is")
    data class I64GeSIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 253
        }
    }

    @CirTag("numeric.i64.ge_s.si")
    data class I64GeSSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 254
        }
    }

    @CirTag("numeric.i64.ge_s.ss")
    data class I64GeSSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 255
        }
    }

    @CirTag("numeric.i64.ge_u.ii")
    data class I64GeUIi(
        val left: Long,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 256
        }
    }

    @CirTag("numeric.i64.ge_u.is")
    data class I64GeUIs(
        val left: Long,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 257
        }
    }

    @CirTag("numeric.i64.ge_u.si")
    data class I64GeUSi(
        val leftSlot: Int,
        val right: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 258
        }
    }

    @CirTag("numeric.i64.ge_u.ss")
    data class I64GeUSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 259
        }
    }

    @CirTag("numeric.i32.extend16_s.i")
    data class I32Extend16SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 260
        }
    }

    @CirTag("numeric.i32.extend16_s.s")
    data class I32Extend16SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 261
        }
    }

    @CirTag("numeric.i32.extend8_s.i")
    data class I32Extend8SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 262
        }
    }

    @CirTag("numeric.i32.extend8_s.s")
    data class I32Extend8SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 263
        }
    }

    @CirTag("numeric.i32.trunc_f32_s.i")
    data class I32TruncF32SI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 264
        }
    }

    @CirTag("numeric.i32.trunc_f32_s.s")
    data class I32TruncF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 265
        }
    }

    @CirTag("numeric.i32.trunc_f32_u.i")
    data class I32TruncF32UI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 266
        }
    }

    @CirTag("numeric.i32.trunc_f32_u.s")
    data class I32TruncF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 267
        }
    }

    @CirTag("numeric.i32.trunc_f64_s.i")
    data class I32TruncF64SI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 268
        }
    }

    @CirTag("numeric.i32.trunc_f64_s.s")
    data class I32TruncF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 269
        }
    }

    @CirTag("numeric.i32.trunc_f64_u.i")
    data class I32TruncF64UI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 270
        }
    }

    @CirTag("numeric.i32.trunc_f64_u.s")
    data class I32TruncF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 271
        }
    }

    @CirTag("numeric.i32.trunc_sat_f32_s.i")
    data class I32TruncSatF32SI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 272
        }
    }

    @CirTag("numeric.i32.trunc_sat_f32_s.s")
    data class I32TruncSatF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 273
        }
    }

    @CirTag("numeric.i32.trunc_sat_f32_u.i")
    data class I32TruncSatF32UI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 274
        }
    }

    @CirTag("numeric.i32.trunc_sat_f32_u.s")
    data class I32TruncSatF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 275
        }
    }

    @CirTag("numeric.i32.trunc_sat_f64_s.i")
    data class I32TruncSatF64SI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 276
        }
    }

    @CirTag("numeric.i32.trunc_sat_f64_s.s")
    data class I32TruncSatF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 277
        }
    }

    @CirTag("numeric.i32.trunc_sat_f64_u.i")
    data class I32TruncSatF64UI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 278
        }
    }

    @CirTag("numeric.i32.trunc_sat_f64_u.s")
    data class I32TruncSatF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 279
        }
    }

    @CirTag("numeric.i32.wrap_i64.i")
    data class I32WrapI64I(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 280
        }
    }

    @CirTag("numeric.i32.wrap_i64.s")
    data class I32WrapI64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 281
        }
    }

    @CirTag("numeric.i64.extend16_s.i")
    data class I64Extend16SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 282
        }
    }

    @CirTag("numeric.i64.extend16_s.s")
    data class I64Extend16SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 283
        }
    }

    @CirTag("numeric.i64.extend32_s.i")
    data class I64Extend32SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 284
        }
    }

    @CirTag("numeric.i64.extend32_s.s")
    data class I64Extend32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 285
        }
    }

    @CirTag("numeric.i64.extend8_s.i")
    data class I64Extend8SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 286
        }
    }

    @CirTag("numeric.i64.extend8_s.s")
    data class I64Extend8SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 287
        }
    }

    @CirTag("numeric.i64.extend_i32_s.i")
    data class I64ExtendI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 288
        }
    }

    @CirTag("numeric.i64.extend_i32_s.s")
    data class I64ExtendI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 289
        }
    }

    @CirTag("numeric.i64.extend_i32_u.i")
    data class I64ExtendI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 290
        }
    }

    @CirTag("numeric.i64.extend_i32_u.s")
    data class I64ExtendI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 291
        }
    }

    @CirTag("numeric.i64.trunc_f32_s.i")
    data class I64TruncF32SI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 292
        }
    }

    @CirTag("numeric.i64.trunc_f32_s.s")
    data class I64TruncF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 293
        }
    }

    @CirTag("numeric.i64.trunc_f32_u.i")
    data class I64TruncF32UI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 294
        }
    }

    @CirTag("numeric.i64.trunc_f32_u.s")
    data class I64TruncF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 295
        }
    }

    @CirTag("numeric.i64.trunc_f64_s.i")
    data class I64TruncF64SI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 296
        }
    }

    @CirTag("numeric.i64.trunc_f64_s.s")
    data class I64TruncF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 297
        }
    }

    @CirTag("numeric.i64.trunc_f64_u.i")
    data class I64TruncF64UI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 298
        }
    }

    @CirTag("numeric.i64.trunc_f64_u.s")
    data class I64TruncF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 299
        }
    }

    @CirTag("numeric.i64.trunc_sat_f32_s.i")
    data class I64TruncSatF32SI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 300
        }
    }

    @CirTag("numeric.i64.trunc_sat_f32_s.s")
    data class I64TruncSatF32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 301
        }
    }

    @CirTag("numeric.i64.trunc_sat_f32_u.i")
    data class I64TruncSatF32UI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 302
        }
    }

    @CirTag("numeric.i64.trunc_sat_f32_u.s")
    data class I64TruncSatF32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 303
        }
    }

    @CirTag("numeric.i64.trunc_sat_f64_s.i")
    data class I64TruncSatF64SI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 304
        }
    }

    @CirTag("numeric.i64.trunc_sat_f64_s.s")
    data class I64TruncSatF64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 305
        }
    }

    @CirTag("numeric.i64.trunc_sat_f64_u.i")
    data class I64TruncSatF64UI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 306
        }
    }

    @CirTag("numeric.i64.trunc_sat_f64_u.s")
    data class I64TruncSatF64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 307
        }
    }

    @CirTag("numeric.f32.add.ii")
    data class F32AddIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 308
        }
    }

    @CirTag("numeric.f32.add.is")
    data class F32AddIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 309
        }
    }

    @CirTag("numeric.f32.add.si")
    data class F32AddSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 310
        }
    }

    @CirTag("numeric.f32.add.ss")
    data class F32AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 311
        }
    }

    @CirTag("numeric.f32.sub.ii")
    data class F32SubIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 312
        }
    }

    @CirTag("numeric.f32.sub.is")
    data class F32SubIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 313
        }
    }

    @CirTag("numeric.f32.sub.si")
    data class F32SubSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 314
        }
    }

    @CirTag("numeric.f32.sub.ss")
    data class F32SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 315
        }
    }

    @CirTag("numeric.f32.mul.ii")
    data class F32MulIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 316
        }
    }

    @CirTag("numeric.f32.mul.is")
    data class F32MulIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 317
        }
    }

    @CirTag("numeric.f32.mul.si")
    data class F32MulSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 318
        }
    }

    @CirTag("numeric.f32.mul.ss")
    data class F32MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 319
        }
    }

    @CirTag("numeric.f32.div.ii")
    data class F32DivIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 320
        }
    }

    @CirTag("numeric.f32.div.is")
    data class F32DivIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 321
        }
    }

    @CirTag("numeric.f32.div.si")
    data class F32DivSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 322
        }
    }

    @CirTag("numeric.f32.div.ss")
    data class F32DivSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 323
        }
    }

    @CirTag("numeric.f32.min.ii")
    data class F32MinIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 324
        }
    }

    @CirTag("numeric.f32.min.is")
    data class F32MinIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 325
        }
    }

    @CirTag("numeric.f32.min.si")
    data class F32MinSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 326
        }
    }

    @CirTag("numeric.f32.min.ss")
    data class F32MinSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 327
        }
    }

    @CirTag("numeric.f32.max.ii")
    data class F32MaxIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 328
        }
    }

    @CirTag("numeric.f32.max.is")
    data class F32MaxIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 329
        }
    }

    @CirTag("numeric.f32.max.si")
    data class F32MaxSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 330
        }
    }

    @CirTag("numeric.f32.max.ss")
    data class F32MaxSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 331
        }
    }

    @CirTag("numeric.f32.copysign.ii")
    data class F32CopysignIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 332
        }
    }

    @CirTag("numeric.f32.copysign.is")
    data class F32CopysignIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 333
        }
    }

    @CirTag("numeric.f32.copysign.si")
    data class F32CopysignSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 334
        }
    }

    @CirTag("numeric.f32.copysign.ss")
    data class F32CopysignSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 335
        }
    }

    @CirTag("numeric.f32.abs.i")
    data class F32AbsI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 336
        }
    }

    @CirTag("numeric.f32.abs.s")
    data class F32AbsS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 337
        }
    }

    @CirTag("numeric.f32.neg.i")
    data class F32NegI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 338
        }
    }

    @CirTag("numeric.f32.neg.s")
    data class F32NegS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 339
        }
    }

    @CirTag("numeric.f32.ceil.i")
    data class F32CeilI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 340
        }
    }

    @CirTag("numeric.f32.ceil.s")
    data class F32CeilS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 341
        }
    }

    @CirTag("numeric.f32.floor.i")
    data class F32FloorI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 342
        }
    }

    @CirTag("numeric.f32.floor.s")
    data class F32FloorS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 343
        }
    }

    @CirTag("numeric.f32.trunc.i")
    data class F32TruncI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 344
        }
    }

    @CirTag("numeric.f32.trunc.s")
    data class F32TruncS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 345
        }
    }

    @CirTag("numeric.f32.nearest.i")
    data class F32NearestI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 346
        }
    }

    @CirTag("numeric.f32.nearest.s")
    data class F32NearestS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 347
        }
    }

    @CirTag("numeric.f32.sqrt.i")
    data class F32SqrtI(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 348
        }
    }

    @CirTag("numeric.f32.sqrt.s")
    data class F32SqrtS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 349
        }
    }

    @CirTag("numeric.f32.eq.ii")
    data class F32EqIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 350
        }
    }

    @CirTag("numeric.f32.eq.is")
    data class F32EqIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 351
        }
    }

    @CirTag("numeric.f32.eq.si")
    data class F32EqSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 352
        }
    }

    @CirTag("numeric.f32.eq.ss")
    data class F32EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 353
        }
    }

    @CirTag("numeric.f32.ne.ii")
    data class F32NeIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 354
        }
    }

    @CirTag("numeric.f32.ne.is")
    data class F32NeIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 355
        }
    }

    @CirTag("numeric.f32.ne.si")
    data class F32NeSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 356
        }
    }

    @CirTag("numeric.f32.ne.ss")
    data class F32NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 357
        }
    }

    @CirTag("numeric.f32.lt.ii")
    data class F32LtIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 358
        }
    }

    @CirTag("numeric.f32.lt.is")
    data class F32LtIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 359
        }
    }

    @CirTag("numeric.f32.lt.si")
    data class F32LtSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 360
        }
    }

    @CirTag("numeric.f32.lt.ss")
    data class F32LtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 361
        }
    }

    @CirTag("numeric.f32.gt.ii")
    data class F32GtIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 362
        }
    }

    @CirTag("numeric.f32.gt.is")
    data class F32GtIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 363
        }
    }

    @CirTag("numeric.f32.gt.si")
    data class F32GtSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 364
        }
    }

    @CirTag("numeric.f32.gt.ss")
    data class F32GtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 365
        }
    }

    @CirTag("numeric.f32.le.ii")
    data class F32LeIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 366
        }
    }

    @CirTag("numeric.f32.le.is")
    data class F32LeIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 367
        }
    }

    @CirTag("numeric.f32.le.si")
    data class F32LeSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 368
        }
    }

    @CirTag("numeric.f32.le.ss")
    data class F32LeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 369
        }
    }

    @CirTag("numeric.f32.ge.ii")
    data class F32GeIi(
        val left: F32,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 370
        }
    }

    @CirTag("numeric.f32.ge.is")
    data class F32GeIs(
        val left: F32,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 371
        }
    }

    @CirTag("numeric.f32.ge.si")
    data class F32GeSi(
        val leftSlot: Int,
        val right: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 372
        }
    }

    @CirTag("numeric.f32.ge.ss")
    data class F32GeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 373
        }
    }

    @CirTag("numeric.f32.convert_i32_s.i")
    data class F32ConvertI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 374
        }
    }

    @CirTag("numeric.f32.convert_i32_s.s")
    data class F32ConvertI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 375
        }
    }

    @CirTag("numeric.f32.convert_i32_u.i")
    data class F32ConvertI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 376
        }
    }

    @CirTag("numeric.f32.convert_i32_u.s")
    data class F32ConvertI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 377
        }
    }

    @CirTag("numeric.f32.convert_i64_s.i")
    data class F32ConvertI64SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 378
        }
    }

    @CirTag("numeric.f32.convert_i64_s.s")
    data class F32ConvertI64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 379
        }
    }

    @CirTag("numeric.f32.convert_i64_u.i")
    data class F32ConvertI64UI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 380
        }
    }

    @CirTag("numeric.f32.convert_i64_u.s")
    data class F32ConvertI64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 381
        }
    }

    @CirTag("numeric.f32.demote_f64.i")
    data class F32DemoteF64I(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 382
        }
    }

    @CirTag("numeric.f32.demote_f64.s")
    data class F32DemoteF64S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 383
        }
    }

    @CirTag("numeric.f64.add.ii")
    data class F64AddIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 384
        }
    }

    @CirTag("numeric.f64.add.is")
    data class F64AddIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 385
        }
    }

    @CirTag("numeric.f64.add.si")
    data class F64AddSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 386
        }
    }

    @CirTag("numeric.f64.add.ss")
    data class F64AddSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 387
        }
    }

    @CirTag("numeric.f64.sub.ii")
    data class F64SubIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 388
        }
    }

    @CirTag("numeric.f64.sub.is")
    data class F64SubIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 389
        }
    }

    @CirTag("numeric.f64.sub.si")
    data class F64SubSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 390
        }
    }

    @CirTag("numeric.f64.sub.ss")
    data class F64SubSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 391
        }
    }

    @CirTag("numeric.f64.mul.ii")
    data class F64MulIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 392
        }
    }

    @CirTag("numeric.f64.mul.is")
    data class F64MulIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 393
        }
    }

    @CirTag("numeric.f64.mul.si")
    data class F64MulSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 394
        }
    }

    @CirTag("numeric.f64.mul.ss")
    data class F64MulSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 395
        }
    }

    @CirTag("numeric.f64.div.ii")
    data class F64DivIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 396
        }
    }

    @CirTag("numeric.f64.div.is")
    data class F64DivIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 397
        }
    }

    @CirTag("numeric.f64.div.si")
    data class F64DivSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 398
        }
    }

    @CirTag("numeric.f64.div.ss")
    data class F64DivSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 399
        }
    }

    @CirTag("numeric.f64.min.ii")
    data class F64MinIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 400
        }
    }

    @CirTag("numeric.f64.min.is")
    data class F64MinIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 401
        }
    }

    @CirTag("numeric.f64.min.si")
    data class F64MinSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 402
        }
    }

    @CirTag("numeric.f64.min.ss")
    data class F64MinSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 403
        }
    }

    @CirTag("numeric.f64.max.ii")
    data class F64MaxIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 404
        }
    }

    @CirTag("numeric.f64.max.is")
    data class F64MaxIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 405
        }
    }

    @CirTag("numeric.f64.max.si")
    data class F64MaxSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 406
        }
    }

    @CirTag("numeric.f64.max.ss")
    data class F64MaxSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 407
        }
    }

    @CirTag("numeric.f64.copysign.ii")
    data class F64CopysignIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 408
        }
    }

    @CirTag("numeric.f64.copysign.is")
    data class F64CopysignIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 409
        }
    }

    @CirTag("numeric.f64.copysign.si")
    data class F64CopysignSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 410
        }
    }

    @CirTag("numeric.f64.copysign.ss")
    data class F64CopysignSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 411
        }
    }

    @CirTag("numeric.f64.abs.i")
    data class F64AbsI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 412
        }
    }

    @CirTag("numeric.f64.abs.s")
    data class F64AbsS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 413
        }
    }

    @CirTag("numeric.f64.neg.i")
    data class F64NegI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 414
        }
    }

    @CirTag("numeric.f64.neg.s")
    data class F64NegS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 415
        }
    }

    @CirTag("numeric.f64.ceil.i")
    data class F64CeilI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 416
        }
    }

    @CirTag("numeric.f64.ceil.s")
    data class F64CeilS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 417
        }
    }

    @CirTag("numeric.f64.floor.i")
    data class F64FloorI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 418
        }
    }

    @CirTag("numeric.f64.floor.s")
    data class F64FloorS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 419
        }
    }

    @CirTag("numeric.f64.trunc.i")
    data class F64TruncI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 420
        }
    }

    @CirTag("numeric.f64.trunc.s")
    data class F64TruncS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 421
        }
    }

    @CirTag("numeric.f64.nearest.i")
    data class F64NearestI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 422
        }
    }

    @CirTag("numeric.f64.nearest.s")
    data class F64NearestS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 423
        }
    }

    @CirTag("numeric.f64.sqrt.i")
    data class F64SqrtI(
        val operand: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 424
        }
    }

    @CirTag("numeric.f64.sqrt.s")
    data class F64SqrtS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 425
        }
    }

    @CirTag("numeric.f64.eq.ii")
    data class F64EqIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 426
        }
    }

    @CirTag("numeric.f64.eq.is")
    data class F64EqIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 427
        }
    }

    @CirTag("numeric.f64.eq.si")
    data class F64EqSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 428
        }
    }

    @CirTag("numeric.f64.eq.ss")
    data class F64EqSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 429
        }
    }

    @CirTag("numeric.f64.ne.ii")
    data class F64NeIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 430
        }
    }

    @CirTag("numeric.f64.ne.is")
    data class F64NeIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 431
        }
    }

    @CirTag("numeric.f64.ne.si")
    data class F64NeSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 432
        }
    }

    @CirTag("numeric.f64.ne.ss")
    data class F64NeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 433
        }
    }

    @CirTag("numeric.f64.lt.ii")
    data class F64LtIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 434
        }
    }

    @CirTag("numeric.f64.lt.is")
    data class F64LtIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 435
        }
    }

    @CirTag("numeric.f64.lt.si")
    data class F64LtSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 436
        }
    }

    @CirTag("numeric.f64.lt.ss")
    data class F64LtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 437
        }
    }

    @CirTag("numeric.f64.gt.ii")
    data class F64GtIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 438
        }
    }

    @CirTag("numeric.f64.gt.is")
    data class F64GtIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 439
        }
    }

    @CirTag("numeric.f64.gt.si")
    data class F64GtSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 440
        }
    }

    @CirTag("numeric.f64.gt.ss")
    data class F64GtSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 441
        }
    }

    @CirTag("numeric.f64.le.ii")
    data class F64LeIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 442
        }
    }

    @CirTag("numeric.f64.le.is")
    data class F64LeIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 443
        }
    }

    @CirTag("numeric.f64.le.si")
    data class F64LeSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 444
        }
    }

    @CirTag("numeric.f64.le.ss")
    data class F64LeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 445
        }
    }

    @CirTag("numeric.f64.ge.ii")
    data class F64GeIi(
        val left: F64,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 446
        }
    }

    @CirTag("numeric.f64.ge.is")
    data class F64GeIs(
        val left: F64,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 447
        }
    }

    @CirTag("numeric.f64.ge.si")
    data class F64GeSi(
        val leftSlot: Int,
        val right: F64,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 448
        }
    }

    @CirTag("numeric.f64.ge.ss")
    data class F64GeSs(
        val leftSlot: Int,
        val rightSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 449
        }
    }

    @CirTag("numeric.f64.convert_i32_s.i")
    data class F64ConvertI32SI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 450
        }
    }

    @CirTag("numeric.f64.convert_i32_s.s")
    data class F64ConvertI32SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 451
        }
    }

    @CirTag("numeric.f64.convert_i32_u.i")
    data class F64ConvertI32UI(
        val operand: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 452
        }
    }

    @CirTag("numeric.f64.convert_i32_u.s")
    data class F64ConvertI32US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 453
        }
    }

    @CirTag("numeric.f64.convert_i64_s.i")
    data class F64ConvertI64SI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 454
        }
    }

    @CirTag("numeric.f64.convert_i64_s.s")
    data class F64ConvertI64SS(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 455
        }
    }

    @CirTag("numeric.f64.convert_i64_u.i")
    data class F64ConvertI64UI(
        val operand: Long,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 456
        }
    }

    @CirTag("numeric.f64.convert_i64_u.s")
    data class F64ConvertI64US(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 457
        }
    }

    @CirTag("numeric.f64.promote_f32.i")
    data class F64PromoteF32I(
        val operand: F32,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 458
        }
    }

    @CirTag("numeric.f64.promote_f32.s")
    data class F64PromoteF32S(
        val operandSlot: Int,
        val destinationSlot: Int,
    ) : NumericInstruction() {
        init {
            opcode = 459
        }
    }
}
