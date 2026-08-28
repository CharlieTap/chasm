package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.type.RTT

sealed interface ControlInstruction : LinkedInstruction {

    data object Unreachable : ControlInstruction

    data class WasmCall(
        val strategy: WasmFunctionCallStrategy,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
    ) : ControlInstruction

    data class HostCall(
        val instance: FunctionInstance.HostFunction,
        val caller: ModuleInstance,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
    ) : ControlInstruction

    data class ReturnWasmCall(
        val strategy: WasmFunctionCallStrategy,
        val operands: OperandTransfer,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction

    data class ReturnHostCall(
        val instance: FunctionInstance.HostFunction,
        val caller: ModuleInstance,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
        val activationHeaderSlot: Int,
    ) : ControlInstruction

    data class CallIndirectI(
        val elementIndex: Int,
        val operands: OperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlInstruction

    data class CallIndirectS(
        val elementIndexSlot: Int,
        val operands: OperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlInstruction

    data class CallRefS(
        val functionSlot: Int,
        val operands: OperandTransfer,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlInstruction

    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: TailCallOperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction

    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: TailCallOperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction

    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: TailCallOperandTransfer,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlInstruction

    data class FunctionReturn(
        val results: OperandTransfer,
        val activationHeaderSlot: Int,
    ) : ControlInstruction

    data class Throw(
        val tagAddress: Address.Tag,
        val firstPayloadSlot: Int,
    ) : ControlInstruction

    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : ControlInstruction
}
