package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.withHostExceptionHandling
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.type.RTT

internal fun CallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirectI,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictIndirectCall(
    vstack = vstack,
    context = context,
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    returnIp = returnIp,
    activationHeader = activationHeader,
    resultDestinationSlot = resultDestinationSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirectS,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictIndirectCall(
    vstack = vstack,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    returnIp = returnIp,
    activationHeader = activationHeader,
    resultDestinationSlot = resultDestinationSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.CallRefS,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictReferenceCall(
    vstack = vstack,
    context = context,
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    returnIp = returnIp,
    activationHeader = activationHeader,
    resultDestinationSlot = resultDestinationSlot,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirectI,
    nextIp: Int,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    context = context,
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
    nextIp = nextIp,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirectS,
    nextIp: Int,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
    nextIp = nextIp,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallRefS,
    nextIp: Int,
): Int = strictReferenceReturnCall(
    vstack = vstack,
    context = context,
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
    nextIp = nextIp,
)

internal fun ThrowExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.Throw,
    faultIp: Int,
): Int {
    return ThrowRefValueExecutor(
        vstack = vstack,
        context = context,
        ref = context.heap.allocateExceptionFromFrame(context, instruction.tagAddress, instruction.firstPayloadSlot),
        faultIp = faultIp,
    )
}

internal fun ThrowRefExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ControlInstruction.ThrowRefS,
    faultIp: Int,
) = ThrowRefValueExecutor(
    vstack = vstack,
    context = context,
    ref = vstack.getFrameSlot(instruction.exceptionSlot),
    faultIp = faultIp,
)

private fun strictIndirectCall(
    vstack: ValueStack,
    context: ExecutionContext,
    elementIndex: Int,
    operands: OperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int?,
): Int {
    val functionInstance = strictResolveIndirectFunction(context, table, type, elementIndex)
    return strictInvokeFunction(
        vstack = vstack,
        context = context,
        functionInstance = functionInstance,
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        returnIp = returnIp,
        activationHeader = activationHeader,
        resultDestinationSlot = resultDestinationSlot,
    )
}

private fun strictReferenceCall(
    vstack: ValueStack,
    context: ExecutionContext,
    functionSlot: Int,
    operands: OperandTransfer,
    caller: ModuleInstance,
    callFrameOffset: Int,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int?,
): Int {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    return strictInvokeFunction(
        vstack = vstack,
        context = context,
        functionInstance = context.store.function(address),
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        returnIp = returnIp,
        activationHeader = activationHeader,
        resultDestinationSlot = resultDestinationSlot,
    )
}

private fun strictIndirectReturnCall(
    vstack: ValueStack,
    context: ExecutionContext,
    elementIndex: Int,
    operands: TailCallOperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
    nextIp: Int,
): Int {
    val functionInstance = strictResolveIndirectFunction(context, table, type, elementIndex)
    return strictInvokeReturnFunction(
        vstack = vstack,
        context = context,
        functionInstance = functionInstance,
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
        nextIp = nextIp,
    )
}

private fun strictReferenceReturnCall(
    vstack: ValueStack,
    context: ExecutionContext,
    functionSlot: Int,
    operands: TailCallOperandTransfer,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
    nextIp: Int,
): Int {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    return strictInvokeReturnFunction(
        vstack = vstack,
        context = context,
        functionInstance = context.store.function(address),
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
        nextIp = nextIp,
    )
}

private fun strictResolveIndirectFunction(
    context: ExecutionContext,
    table: TableInstance,
    type: RTT,
    elementIndex: Int,
): FunctionInstance {
    val address = table.element(elementIndex).toFunctionAddress()
    val functionInstance = context.store.function(address)
    if (!context.heap.matchesRuntimeType(functionInstance.rtt, type)) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }
    return functionInstance
}

private fun strictInvokeFunction(
    vstack: ValueStack,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    caller: ModuleInstance,
    operands: OperandTransfer,
    callFrameOffset: Int,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int?,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        val fp = vstack.fp
        vstack.transferOperands(
            currentFp = fp,
            destinationFp = fp + callFrameOffset,
            transfer = operands,
        )
        withHostExceptionHandling(vstack, context, returnIp) {
            HostFunctionCall(
                vstack = vstack,
                context = context,
                caller = caller,
                function = functionInstance,
                parameterSlotBase = callFrameOffset,
                resultSlotBase = resultDestinationSlot ?: callFrameOffset,
            )
            returnIp
        }
    }
    is FunctionInstance.WasmFunction -> WasmFunctionCall(
        vstack = vstack,
        strategy = functionInstance.callStrategy,
        operands = operands,
        callFrameOffset = callFrameOffset,
        activationHeader = activationHeader,
    )
}

private fun strictInvokeReturnFunction(
    vstack: ValueStack,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    caller: ModuleInstance,
    operands: TailCallOperandTransfer,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
    nextIp: Int,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        val fp = vstack.fp
        val parameterBase = fp + callFrameOffset
        vstack.transferOperands(
            currentFp = fp,
            destinationFp = parameterBase,
            transfer = operands.host,
        )
        withHostExceptionHandling(vstack, context, nextIp) {
            HostFunctionCall(
                vstack = vstack,
                context = context,
                caller = caller,
                function = functionInstance,
                parameterSlotBase = callFrameOffset,
                resultSlotBase = 0,
            )
            ReturnExecutor(
                vstack,
                context,
                functionInstance.functionType.results.types.size,
                callerActivationHeaderSlot,
            )
        }
    }
    is FunctionInstance.WasmFunction -> ReturnWasmFunctionCall(
        vstack = vstack,
        strategy = functionInstance.callStrategy,
        operands = operands.wasm,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
    )
}
