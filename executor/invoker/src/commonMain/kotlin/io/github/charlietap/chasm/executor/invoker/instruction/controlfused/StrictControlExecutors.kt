package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor as ControlThrowRefExecutor

internal fun CallExecutor(
    vstack: ValueStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallIndirectI,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictIndirectCall(
    vstack = vstack,
    store = store,
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
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallIndirectS,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictIndirectCall(
    vstack = vstack,
    store = store,
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
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallRefS,
    returnIp: Int,
    activationHeader: Long,
    resultDestinationSlot: Int? = null,
): Int = strictReferenceCall(
    vstack = vstack,
    store = store,
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
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallIndirectI,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    store = store,
    context = context,
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallIndirectS,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    store = store,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallRefS,
): Int = strictReferenceReturnCall(
    vstack = vstack,
    store = store,
    context = context,
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
    caller = instruction.caller,
    callFrameOffset = instruction.callFrameOffset,
    callerActivationHeaderSlot = instruction.callerActivationHeaderSlot,
)

internal fun ThrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.Throw,
): Int {
    return ControlThrowRefExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        ref = context.heap.allocateExceptionFromFrame(context, instruction.tagAddress, instruction.firstPayloadSlot),
    )
}

internal fun ThrowRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    instruction: ControlSuperInstruction.ThrowRefS,
) = ControlThrowRefExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    ref = vstack.getFrameSlot(instruction.exceptionSlot),
)

private fun strictIndirectCall(
    vstack: ValueStack,
    store: Store,
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
    val functionInstance = strictResolveIndirectFunction(store, context.heap, table, type, elementIndex)
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
    store: Store,
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
        functionInstance = store.function(address),
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
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    operands: TailCallOperandTransfer,
    type: RTT,
    table: TableInstance,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
): Int {
    val functionInstance = strictResolveIndirectFunction(store, context.heap, table, type, elementIndex)
    return strictInvokeReturnFunction(
        vstack = vstack,
        context = context,
        functionInstance = functionInstance,
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
    )
}

private fun strictReferenceReturnCall(
    vstack: ValueStack,
    store: Store,
    context: ExecutionContext,
    functionSlot: Int,
    operands: TailCallOperandTransfer,
    caller: ModuleInstance,
    callFrameOffset: Int,
    callerActivationHeaderSlot: Int,
): Int {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    return strictInvokeReturnFunction(
        vstack = vstack,
        context = context,
        functionInstance = store.function(address),
        caller = caller,
        operands = operands,
        callFrameOffset = callFrameOffset,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
    )
}

private fun strictResolveIndirectFunction(
    store: Store,
    heap: WasmHeap,
    table: TableInstance,
    type: RTT,
    elementIndex: Int,
): FunctionInstance {
    val address = table.element(elementIndex).toFunctionAddress()
    val functionInstance = store.function(address)
    if (!heap.matchesRuntimeType(functionInstance.rtt, type)) {
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
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        val fp = vstack.fp
        val parameterBase = fp + callFrameOffset
        vstack.transferOperands(
            currentFp = fp,
            destinationFp = parameterBase,
            transfer = operands.host,
        )
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
            context.store,
            functionInstance.functionType.results.types.size,
            callerActivationHeaderSlot,
        )
    }
    is FunctionInstance.WasmFunction -> ReturnWasmFunctionCall(
        vstack = vstack,
        strategy = functionInstance.callStrategy,
        operands = operands.wasm,
        callerActivationHeaderSlot = callerActivationHeaderSlot,
    )
}
