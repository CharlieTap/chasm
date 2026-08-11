package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.RTT

sealed interface ControlSuperInstruction : LinkedInstruction {

    data class WasmCall(
        val plan: WasmFunctionCallPlan,
        val operands: OperandCopyPlan,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class HostCall(
        val instance: FunctionInstance.HostFunction,
        val operands: OperandCopyPlan,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnWasmCall(
        val plan: WasmFunctionCallPlan,
        val operands: List<CopyOperand>,
    ) : ControlSuperInstruction

    data class ReturnHostCall(
        val instance: FunctionInstance.HostFunction,
        val operands: List<CopyOperand>,
    ) : ControlSuperInstruction

    data class CallIndirectI(
        val elementIndex: Int,
        val operands: OperandCopyPlan,
        val type: RTT,
        val table: TableInstance,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class CallIndirectS(
        val elementIndexSlot: Int,
        val operands: OperandCopyPlan,
        val type: RTT,
        val table: TableInstance,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class CallRefS(
        val functionSlot: Int,
        val operands: OperandCopyPlan,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: List<CopyOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: List<CopyOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : ControlSuperInstruction

    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: List<CopyOperand>,
    ) : ControlSuperInstruction

    data class FunctionReturn(
        val results: OperandCopyPlan,
    ) : ControlSuperInstruction

    data class Throw(
        val tagIndex: Index.TagIndex,
        val payloadSlots: IntArray,
    ) : ControlSuperInstruction

    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : ControlSuperInstruction
}
