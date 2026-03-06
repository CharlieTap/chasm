package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrDispatcher
import io.github.charlietap.chasm.executor.invoker.ext.tagAddress
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnHostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.instruction.control.BlockExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.control.BreakExecutor
import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher as LegacyThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefValueExecutor as LegacyThrowRefExecutor

internal fun BrIfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrIfI,
) = BrIfExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    labelIndex = instruction.labelIndex,
    takenInstructions = instruction.takenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal fun BrIfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrIfS,
) = BrIfExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    labelIndex = instruction.labelIndex,
    takenInstructions = instruction.takenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrIfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
    takenInstructions: List<DispatchableInstruction>,
    crossinline breakExecutor: BreakExecutor,
) {
    if (operand != 0L) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        breakExecutor(cstack, vstack, labelIndex)
    }
}

internal fun BrTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrTableI,
) = BrTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    labelIndices = instruction.labelIndices,
    defaultLabelIndex = instruction.defaultLabelIndex,
    takenInstructions = instruction.takenInstructions,
    defaultTakenInstructions = instruction.defaultTakenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal fun BrTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrTableS,
) = BrTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot).toInt(),
    labelIndices = instruction.labelIndices,
    defaultLabelIndex = instruction.defaultLabelIndex,
    takenInstructions = instruction.takenInstructions,
    defaultTakenInstructions = instruction.defaultTakenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Int,
    labelIndices: List<io.github.charlietap.chasm.ir.module.Index.LabelIndex>,
    defaultLabelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
    takenInstructions: List<List<DispatchableInstruction>>,
    defaultTakenInstructions: List<DispatchableInstruction>,
    crossinline breakExecutor: BreakExecutor,
) {
    val targetIndex = if (operand >= 0 && operand < labelIndices.size) {
        operand
    } else {
        -1
    }
    val label = if (targetIndex >= 0) {
        labelIndices[targetIndex]
    } else {
        defaultLabelIndex
    }
    val selectedTakenInstructions = if (targetIndex >= 0) {
        takenInstructions[targetIndex]
    } else {
        defaultTakenInstructions
    }

    executeTakenInstructions(vstack, cstack, store, context, selectedTakenInstructions)
    breakExecutor(cstack, vstack, label)
}

internal fun BrOnNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrOnNullS,
) = BrOnNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    labelIndex = instruction.labelIndex,
    takenInstructions = instruction.takenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
    takenInstructions: List<DispatchableInstruction>,
    crossinline breakExecutor: BreakExecutor,
) {
    if (operand.isNullableReference()) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        breakExecutor(cstack, vstack, labelIndex)
    }
}

internal fun BrOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrOnNonNullS,
) = BrOnNonNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    labelIndex = instruction.labelIndex,
    takenInstructions = instruction.takenInstructions,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
    takenInstructions: List<DispatchableInstruction>,
    crossinline breakExecutor: BreakExecutor,
) {
    if (!operand.isNullableReference()) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        breakExecutor(cstack, vstack, labelIndex)
    }
}

internal fun BrOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrOnCastS,
) = BrOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    labelIndex = instruction.labelIndex,
    referenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    breakIfMatches = true,
    caster = ::Caster,
    breakExecutor = ::BreakExecutor,
)

internal fun BrOnCastFailExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrOnCastFailS,
) = BrOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    labelIndex = instruction.labelIndex,
    referenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    breakIfMatches = false,
    caster = ::Caster,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    labelIndex: io.github.charlietap.chasm.ir.module.Index.LabelIndex,
    referenceType: ReferenceType,
    takenInstructions: List<DispatchableInstruction>,
    breakIfMatches: Boolean,
    crossinline caster: Caster,
    crossinline breakExecutor: BreakExecutor,
) {
    val moduleInstance = cstack.peekFrame().instance
    val casted = caster(operand, referenceType, moduleInstance, store)
    if (casted == breakIfMatches) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        breakExecutor(cstack, vstack, labelIndex)
    }
}

private inline fun executeTakenInstructions(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instructions: List<DispatchableInstruction>,
) {
    instructions.forEach { instruction ->
        instruction(vstack, cstack, store, context)
    }
}

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.WasmCall,
) = WasmFunctionCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instance = instruction.instance,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.HostCall,
) = HostFunctionCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    function = instruction.instance,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.CallIndirectI,
) = strictIndirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = instruction.elementIndex,
    type = instruction.type,
    table = instruction.table,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.CallIndirectS,
) = strictIndirectCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    elementIndex = vstack.getFrameSlot(instruction.elementIndexSlot).toInt(),
    type = instruction.type,
    table = instruction.table,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
)

internal fun CallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.CallRefS,
) = strictReferenceCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    functionSlot = instruction.functionSlot,
    resultSlots = instruction.resultSlots,
    callFrameSlot = instruction.callFrameSlot,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.ReturnWasmCall,
) = ReturnWasmFunctionCall(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instance = instruction.instance,
    operands = instruction.operands,
)

internal fun ReturnCallExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.ReturnHostCall,
) = ReturnHostFunctionCall(
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
    instruction: FusedControlInstruction.ReturnCallIndirectI,
) = strictIndirectReturnCall(
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
    instruction: FusedControlInstruction.ReturnCallIndirectS,
) = strictIndirectReturnCall(
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
    instruction: FusedControlInstruction.ReturnCallRefS,
) = strictReferenceReturnCall(
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
    context: ExecutionContext,
    instruction: FusedControlInstruction.Throw,
) {
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
    LegacyThrowRefExecutor(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        ref = ReferenceValue.Exception(exceptionAddress).toLong(),
        breakDispatcher = ::BrDispatcher,
        throwRefDispatcher = ::LegacyThrowRefDispatcher,
    )
}

internal fun ThrowRefExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.ThrowRefS,
) = LegacyThrowRefExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    ref = vstack.getFrameSlot(instruction.exceptionSlot),
    breakDispatcher = ::BrDispatcher,
    throwRefDispatcher = ::LegacyThrowRefDispatcher,
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
) {
    val functionInstance = strictResolveIndirectFunction(store, table, type, elementIndex)
    strictInvokeFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = functionInstance,
        resultSlots = resultSlots,
        callFrameSlot = callFrameSlot,
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
) {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    strictInvokeFunction(
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        functionInstance = store.function(address),
        resultSlots = resultSlots,
        callFrameSlot = callFrameSlot,
    )
}

private fun strictIndirectReturnCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    elementIndex: Int,
    operands: List<FusedControlInstruction.CallOperand>,
    type: RTT,
    table: TableInstance,
) {
    val functionInstance = strictResolveIndirectFunction(store, table, type, elementIndex)
    strictInvokeReturnFunction(
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
    operands: List<FusedControlInstruction.CallOperand>,
) {
    val address = vstack.getFrameSlot(functionSlot).toFunctionAddress()
    strictInvokeReturnFunction(
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
) {
    when (functionInstance) {
        is FunctionInstance.HostFunction -> HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
            resultSlots = resultSlots,
            callFrameSlot = callFrameSlot,
        )
        is FunctionInstance.WasmFunction -> WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
            resultSlots = resultSlots,
            callFrameSlot = callFrameSlot,
        )
    }
}

private fun strictInvokeReturnFunction(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    functionInstance: FunctionInstance,
    operands: List<FusedControlInstruction.CallOperand>,
) {
    when (functionInstance) {
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
}

internal fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.IfI,
) = IfExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    params = instruction.params,
    results = instruction.results,
    instructions = instruction.instructions,
    blockExecutor = ::BlockExecutor,
)

internal fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.IfS,
) = IfExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    params = instruction.params,
    results = instruction.results,
    instructions = instruction.instructions,
    blockExecutor = ::BlockExecutor,
)

internal inline fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    params: Int,
    results: Int,
    instructions: Array<Array<DispatchableInstruction>>,
    crossinline blockExecutor: BlockExecutor,
) {
    val branchIndex = ((operand or -operand) ushr 63).toInt()
    blockExecutor(store, cstack, vstack, params, results, instructions[branchIndex])
}
