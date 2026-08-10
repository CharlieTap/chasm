package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnHostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor as ControlThrowRefExecutor

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.HostCall,
    returnIp: Int,
): Int {
    HostFunctionCall(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        function = instruction.instance,
        resultSlots = instruction.resultSlots,
        callFrameSlot = instruction.callFrameSlot,
    )
    return returnIp
}

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallIndirectI,
    returnIp: Int,
): Int = strictIndirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = instruction.elementIndex,
    type = instruction.type,
    table = instruction.table,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
    returnIp = returnIp,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallIndirectS,
    returnIp: Int,
): Int = strictIndirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    type = instruction.type,
    table = instruction.table,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
    returnIp = returnIp,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.CallRefS,
    returnIp: Int,
): Int = strictReferenceCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionSlot = instruction.functionSlot,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
    returnIp = returnIp,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnWasmCall,
): Int = ReturnWasmFunctionCall(
    vstack = vstack,
    cstack = cstack,
    plan = instruction.plan,
    operands = instruction.operands,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnHostCall,
): Int = ReturnHostFunctionCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    function = instruction.instance,
    operands = instruction.operands,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallIndirectI,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = instruction.elementIndex,
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallIndirectS,
): Int = strictIndirectReturnCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    operands = instruction.operands,
    type = instruction.type,
    table = instruction.table,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlSuperInstruction.ReturnCallRefS,
): Int = strictReferenceReturnCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionSlot = instruction.functionSlot,
    operands = instruction.operands,
)

internal fun ThrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    instruction: ControlSuperInstruction.Throw,
): Int {
    val frame = cstack.peekFrame()
    val address = frame.instance.tagAddress(instruction.tagIndex)
    val params = LongArray(instruction.payloadSlots.size) { index ->
        val sourceIndex = instruction.payloadSlots.lastIndex - index
        vstack.getFrameSlot(instruction.payloadSlots[sourceIndex])
    }
    val exceptionInstance = ExceptionInstance(
        tagAddress = address,
        fields = params,
    )

    store.exceptions.add(exceptionInstance)
    val exceptionAddress = Address.Exception(store.exceptions.size - 1)
    return ControlThrowRefExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        ref = ReferenceValue.Exception(exceptionAddress).toLong(),
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
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    type: RTT,
    table: TableInstance,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val functionInstance = strictResolveIndirectFunction(store, table, type, elementIndex)
    return strictInvokeFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = functionInstance,
        resultSlots = resultSlots,
        callFrameSlot = callFrameSlot,
        returnIp = returnIp,
    )
}

private fun strictReferenceCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionSlot: Int,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    returnIp: Int,
): Int {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    return strictInvokeFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = store.function(address),
        resultSlots = resultSlots,
        callFrameSlot = callFrameSlot,
        returnIp = returnIp,
    )
}

private fun strictIndirectReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
    type: RTT,
    table: TableInstance,
): Int {
    val functionInstance = strictResolveIndirectFunction(store, table, type, elementIndex)
    return strictInvokeReturnFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = functionInstance,
        operands = operands,
    )
}

private fun strictReferenceReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionSlot: Int,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    return strictInvokeReturnFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = store.function(address),
        operands = operands,
    )
}

private fun strictResolveIndirectFunction(
    store: Store,
    table: TableInstance,
    type: RTT,
    elementIndex: Int,
): FunctionInstance {
    val address = table.element(elementIndex).toFunctionAddress()
    val functionInstance = store.function(address)
    val actualType = functionInstance.rtt
    if (actualType !== type && actualType.superTypes.none { superType -> superType === type }) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }
    return functionInstance
}

private fun strictInvokeFunction(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    resultSlots: List<Int>,
    callFrameSlot: Int,
    returnIp: Int,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> {
        HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
            resultSlots = resultSlots,
            callFrameSlot = callFrameSlot,
        )
        returnIp
    }
    is FunctionInstance.WasmFunction -> WasmFunctionCall(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instance = functionInstance,
        resultSlots = resultSlots,
        callFrameSlot = callFrameSlot,
        returnIp = returnIp,
    )
}

private fun strictInvokeReturnFunction(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    operands: List<ControlSuperInstruction.CallOperand>,
): Int = when (functionInstance) {
    is FunctionInstance.HostFunction -> ReturnHostFunctionCall(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        function = functionInstance,
        operands = operands,
    )
    is FunctionInstance.WasmFunction -> ReturnWasmFunctionCall(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instance = functionInstance,
        operands = operands,
    )
}
