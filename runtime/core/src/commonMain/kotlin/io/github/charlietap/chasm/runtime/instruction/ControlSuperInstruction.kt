package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.type.RTT

sealed interface ControlSuperInstruction : LinkedInstruction {

    data class WasmCall(
        val strategy: WasmFunctionCallStrategy,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
    ) : ControlSuperInstruction

    data class HostCall(
        val instance: FunctionInstance.HostFunction,
        val caller: ModuleInstance,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
    ) : ControlSuperInstruction

    data class ReturnWasmCall(
        val strategy: WasmFunctionCallStrategy,
        val operands: OperandTransfer,
        val callerActivationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnHostCall(
        val instance: FunctionInstance.HostFunction,
        val caller: ModuleInstance,
        val operands: OperandTransfer,
        val callFrameOffset: Int,
        val activationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class CallIndirectI(
        val elementIndex: Int,
        val operands: OperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlSuperInstruction

    data class CallIndirectS(
        val elementIndexSlot: Int,
        val operands: OperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlSuperInstruction

    data class CallRefS(
        val functionSlot: Int,
        val operands: OperandTransfer,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectI(
        val elementIndex: Int,
        val operands: TailCallOperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnCallIndirectS(
        val elementIndexSlot: Int,
        val operands: TailCallOperandTransfer,
        val type: RTT,
        val table: TableInstance,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class ReturnCallRefS(
        val functionSlot: Int,
        val operands: TailCallOperandTransfer,
        val caller: ModuleInstance,
        val callFrameOffset: Int,
        val callerActivationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class FunctionReturn(
        val results: OperandTransfer,
        val activationHeaderSlot: Int,
    ) : ControlSuperInstruction

    data class Throw(
        val tagAddress: Address.Tag,
        val firstPayloadSlot: Int,
    ) : ControlSuperInstruction

    data class ThrowRefS(
        val exceptionSlot: Int,
    ) : ControlSuperInstruction
}
