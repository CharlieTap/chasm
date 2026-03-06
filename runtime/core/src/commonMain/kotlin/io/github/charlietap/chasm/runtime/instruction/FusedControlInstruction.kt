package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface FusedControlInstruction : LinkedInstruction {

    sealed interface CallOperand {

        @JvmInline
        value class Immediate(val value: Long) : CallOperand

        @JvmInline
        value class Slot(val slot: Int) : CallOperand
    }

    data class BrIfI(
        val operand: Long,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrIfS(
        val operandSlot: Int,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrTableI(
        val operand: Int,
        val labelIndices: List<Index.LabelIndex>,
        val defaultLabelIndex: Index.LabelIndex,
        val takenInstructions: List<List<DispatchableInstruction>>,
        val defaultTakenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrTableS(
        val operandSlot: Int,
        val labelIndices: List<Index.LabelIndex>,
        val defaultLabelIndex: Index.LabelIndex,
        val takenInstructions: List<List<DispatchableInstruction>>,
        val defaultTakenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrOnNullS(
        val operandSlot: Int,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrOnNonNullS(
        val operandSlot: Int,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrOnCastS(
        val operandSlot: Int,
        val labelIndex: Index.LabelIndex,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class BrOnCastFailS(
        val operandSlot: Int,
        val labelIndex: Index.LabelIndex,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<DispatchableInstruction>,
    ) : FusedControlInstruction

    data class WasmCall(
        val instance: FunctionInstance.WasmFunction,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : FusedControlInstruction

    data class HostCall(
        val instance: FunctionInstance.HostFunction,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : FusedControlInstruction

    data class ReturnWasmCall(
        val instance: FunctionInstance.WasmFunction,
        val operands: List<CallOperand>,
    ) : FusedControlInstruction

    data class ReturnHostCall(
        val instance: FunctionInstance.HostFunction,
        val operands: List<CallOperand>,
    ) : FusedControlInstruction

    data class CallIndirectI(
        val elementIndex: Int,
        val type: RTT,
        val table: TableInstance,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : FusedControlInstruction

    data class CallIndirectS(
        val elementIndexSlot: Int,
        val type: RTT,
        val table: TableInstance,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : FusedControlInstruction

    data class CallRefS(
        val functionSlot: Int,
        val resultSlots: List<Int>,
        val callFrameSlot: Int,
    ) : FusedControlInstruction

    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: List<CallOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : FusedControlInstruction

    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: List<CallOperand>,
        val type: RTT,
        val table: TableInstance,
    ) : FusedControlInstruction

    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: List<CallOperand>,
    ) : FusedControlInstruction

    data class Throw(
        val tagIndex: Index.TagIndex,
        val payloadSlots: List<Int>,
    ) : FusedControlInstruction

    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : FusedControlInstruction

    data class IfI(
        val operand: Long,
        val params: Int,
        val results: Int,
        val instructions: Array<Array<DispatchableInstruction>>,
    ) : FusedControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as IfI

            if (operand != other.operand) return false
            if (params != other.params) return false
            if (results != other.results) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = operand.hashCode()
            result = 31 * result + params
            result = 31 * result + results
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class IfS(
        val operandSlot: Int,
        val params: Int,
        val results: Int,
        val instructions: Array<Array<DispatchableInstruction>>,
    ) : FusedControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as IfS

            if (operandSlot != other.operandSlot) return false
            if (params != other.params) return false
            if (results != other.results) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = operandSlot
            result = 31 * result + params
            result = 31 * result + results
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }
}
