package io.github.charlietap.chasm.cir

sealed class ControlInstruction : Instruction() {

    @CirTag("control.unreachable")
    data object Unreachable : ControlInstruction() {
        init {
            opcode = 672
        }
    }

    @CirTag("control.call")
    data class Call(
        val function: FunctionIndex,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
        val resultDestinationSlot: Int?,
    ) : ControlInstruction() {
        init {
            opcode = 673
        }
    }

    @CirTag("control.return_call")
    data class ReturnCall(
        val function: FunctionIndex,
        val operands: TailCallOperandTransfer,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 674
        }
    }

    @CirTag("control.call_indirect.i")
    data class CallIndirectI(
        val elementIndex: Int,
        val operands: OperandTransfer,
        val type: TypeIndex,
        val table: TableIndex,
        val callFrameOffset: Int,
        val resultDestinationSlot: Int?,
    ) : ControlInstruction() {
        init {
            opcode = 675
        }
    }

    @CirTag("control.call_indirect.s")
    data class CallIndirectS(
        val elementIndexSlot: Int,
        val operands: OperandTransfer,
        val type: TypeIndex,
        val table: TableIndex,
        val callFrameOffset: Int,
        val resultDestinationSlot: Int?,
    ) : ControlInstruction() {
        init {
            opcode = 676
        }
    }

    @CirTag("control.call_ref")
    data class CallRefS(
        val functionSlot: Int,
        val operands: OperandTransfer,
        val type: TypeIndex,
        val callFrameOffset: Int,
        val resultDestinationSlot: Int?,
    ) : ControlInstruction() {
        init {
            opcode = 677
        }
    }

    @CirTag("control.return_call_indirect.i")
    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: TailCallOperandTransfer,
        val type: TypeIndex,
        val table: TableIndex,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 678
        }
    }

    @CirTag("control.return_call_indirect.s")
    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: TailCallOperandTransfer,
        val type: TypeIndex,
        val table: TableIndex,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 679
        }
    }

    @CirTag("control.return_call_ref")
    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: TailCallOperandTransfer,
        val type: TypeIndex,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 680
        }
    }

    @CirTag("control.return")
    data class FunctionReturn(
        val results: OperandTransfer,
        val activationHeaderSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 681
        }
    }

    @CirTag("control.throw")
    data class Throw(
        val exceptionTag: TagIndex,
        val firstPayloadSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 682
        }
    }

    @CirTag("control.throw_ref")
    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : ControlInstruction() {
        init {
            opcode = 683
        }
    }
}
