package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallPlan
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.RTT
import kotlin.jvm.JvmInline

sealed interface ControlSuperInstruction : LinkedInstruction {

    sealed interface CallOperand {

        @JvmInline
        value class Immediate(val value: Long) : CallOperand

        @JvmInline
        value class Slot(val slot: Int) : CallOperand
    }

    data class WasmCall(
        val plan: WasmFunctionCallPlan,
        val resultSlotBase: Int,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class HostCall(
        val instance: FunctionInstance.HostFunction,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnWasmCall(
        val plan: WasmFunctionCallPlan,
        val operands: List<CallOperand>,
    ) : ControlSuperInstruction

    data class ReturnHostCall(
        val instance: FunctionInstance.HostFunction,
        val operands: List<CallOperand>,
    ) : ControlSuperInstruction

    data class CallIndirectI(
        val elementIndex: Int,
        val type: RTT,
        val table: TableInstance,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class CallIndirectS(
        val elementIndexSlot: Int,
        val type: RTT,
        val table: TableInstance,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class CallRefS(
        val functionSlot: Int,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: List<CallOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: List<CallOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : ControlSuperInstruction

    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: List<CallOperand>,
    ) : ControlSuperInstruction

    data class Throw(
        val tagIndex: Index.TagIndex,
        val payloadSlots: List<Int>,
    ) : ControlSuperInstruction

    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : ControlSuperInstruction
}
