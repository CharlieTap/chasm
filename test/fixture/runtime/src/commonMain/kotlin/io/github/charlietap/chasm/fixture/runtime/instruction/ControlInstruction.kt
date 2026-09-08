package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress as tagAddressFixture

fun controlRuntimeInstruction(): ControlInstruction = unreachableRuntimeInstruction()

fun unreachableRuntimeInstruction() = ControlInstruction.Unreachable

fun wasmCallRuntimeInstruction(
    strategy: WasmFunctionCallStrategy = wasmFunctionCallStrategy(),
    operands: OperandTransfer = operandTransfer(),
    callFrameOffset: Int = 0,
) = ControlInstruction.WasmCall(
    strategy = strategy,
    operands = operands,
    callFrameOffset = callFrameOffset,
)

fun hostCallRuntimeInstruction(
    instance: FunctionInstance.HostFunction = hostFunctionInstance(),
    caller: ModuleInstance = moduleInstance(),
    operands: OperandTransfer = operandTransfer(),
    callFrameOffset: Int = 0,
) = ControlInstruction.HostCall(
    instance = instance,
    caller = caller,
    operands = operands,
    callFrameOffset = callFrameOffset,
)

fun returnWasmCallRuntimeInstruction(
    strategy: WasmFunctionCallStrategy = wasmFunctionCallStrategy(),
    operands: OperandTransfer = operandTransfer(),
    callerActivationHeaderSlot: Int = 0,
) = ControlInstruction.ReturnWasmCall(
    strategy = strategy,
    operands = operands,
    callerActivationHeaderSlot = callerActivationHeaderSlot,
)

fun returnHostCallRuntimeInstruction(
    instance: FunctionInstance.HostFunction = hostFunctionInstance(),
    caller: ModuleInstance = moduleInstance(),
    operands: OperandTransfer = operandTransfer(),
    callFrameOffset: Int = 0,
    activationHeaderSlot: Int = 0,
) = ControlInstruction.ReturnHostCall(
    instance = instance,
    caller = caller,
    operands = operands,
    callFrameOffset = callFrameOffset,
    activationHeaderSlot = activationHeaderSlot,
)

fun callIndirectIRuntimeInstruction(
    elementIndex: Int = 0,
    operands: OperandTransfer = operandTransfer(),
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
) = ControlInstruction.CallIndirectI(
    elementIndex = elementIndex,
    operands = operands,
    type = type,
    table = table,
    caller = caller,
    callFrameOffset = callFrameOffset,
)

fun callIndirectSRuntimeInstruction(
    elementIndexSlot: Int = 0,
    operands: OperandTransfer = operandTransfer(),
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
) = ControlInstruction.CallIndirectS(
    elementIndexSlot = elementIndexSlot,
    operands = operands,
    type = type,
    table = table,
    caller = caller,
    callFrameOffset = callFrameOffset,
)

fun callRefSRuntimeInstruction(
    functionSlot: Int = 0,
    operands: OperandTransfer = operandTransfer(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
) = ControlInstruction.CallRefS(
    functionSlot = functionSlot,
    operands = operands,
    caller = caller,
    callFrameOffset = callFrameOffset,
)

fun returnCallIndirectIRuntimeInstruction(
    elementIndex: Int = 0,
    operands: TailCallOperandTransfer = tailCallOperandTransfer(),
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
    callerActivationHeaderSlot: Int = 0,
) = ControlInstruction.ReturnCallIndirectI(
    elementIndex = elementIndex,
    operands = operands,
    type = type,
    table = table,
    caller = caller,
    callFrameOffset = callFrameOffset,
    callerActivationHeaderSlot = callerActivationHeaderSlot,
)

fun returnCallIndirectSRuntimeInstruction(
    elementIndexSlot: Int = 0,
    operands: TailCallOperandTransfer = tailCallOperandTransfer(),
    type: RTT = rtt(),
    table: TableInstance = tableInstance(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
    callerActivationHeaderSlot: Int = 0,
) = ControlInstruction.ReturnCallIndirectS(
    elementIndexSlot = elementIndexSlot,
    operands = operands,
    type = type,
    table = table,
    caller = caller,
    callFrameOffset = callFrameOffset,
    callerActivationHeaderSlot = callerActivationHeaderSlot,
)

fun returnCallRefSRuntimeInstruction(
    functionSlot: Int = 0,
    operands: TailCallOperandTransfer = tailCallOperandTransfer(),
    caller: ModuleInstance = moduleInstance(),
    callFrameOffset: Int = 0,
    callerActivationHeaderSlot: Int = 0,
) = ControlInstruction.ReturnCallRefS(
    functionSlot = functionSlot,
    operands = operands,
    caller = caller,
    callFrameOffset = callFrameOffset,
    callerActivationHeaderSlot = callerActivationHeaderSlot,
)

fun functionReturnRuntimeInstruction(
    results: OperandTransfer = operandTransfer(),
    activationHeaderSlot: Int = 0,
) = ControlInstruction.FunctionReturn(
    results = results,
    activationHeaderSlot = activationHeaderSlot,
)

fun throwRuntimeInstruction(
    tagAddress: Address.Tag = tagAddressFixture(),
    firstPayloadSlot: Int = 0,
) = ControlInstruction.Throw(
    tagAddress = tagAddress,
    firstPayloadSlot = firstPayloadSlot,
)

fun throwRefSRuntimeInstruction(
    exceptionSlot: Int = 0,
) = ControlInstruction.ThrowRefS(
    exceptionSlot = exceptionSlot,
)

private fun wasmFunctionCallStrategy() = WasmFunctionCallStrategy(interfaceSlotCount = 0)

private fun tailCallOperandTransfer() = TailCallOperandTransfer(
    wasm = operandTransfer(),
    host = operandTransfer(),
)
