package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias StackFunctionCall = (ValueStack, ControlStack, Store, ExecutionContext, FunctionInstance.StackFunction) -> Unit

internal inline fun StackFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.StackFunction,
) {
    val type = function.functionType
    val params = type.params.types.size
    val results = type.results.types.size
    val interfaceSlots = maxOf(params, results)
    val previousFramePointer = vstack.framePointer
    val interfaceFramePointer = vstack.depth() - params

    vstack.framePointer = interfaceFramePointer
    vstack.reserveDepth(interfaceFramePointer + interfaceSlots)
    try {
        function.body(vstack, cstack, store, context)
        vstack.shrink(
            preserveTopN = 0,
            depth = interfaceFramePointer + results,
        )
    } finally {
        vstack.framePointer = previousFramePointer
    }
}

internal inline fun StackFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.StackFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
) {
    val type = function.functionType
    val interfaceSlots = maxOf(type.params.types.size, type.results.types.size)
    val previousFramePointer = vstack.framePointer
    val previousDepth = vstack.depth()
    val interfaceFramePointer = previousFramePointer + callFrameSlot

    vstack.framePointer = interfaceFramePointer
    vstack.reserveDepth(interfaceFramePointer + interfaceSlots)
    try {
        function.body(vstack, cstack, store, context)
        type.results.types.indices.forEach { index ->
            val value = vstack.getFrameSlot(index)
            vstack.setFrameSlot(previousFramePointer, resultSlots[index], value)
        }
        vstack.shrink(
            preserveTopN = 0,
            depth = previousDepth,
        )
    } finally {
        vstack.framePointer = previousFramePointer
    }
}

internal inline fun ReturnStackFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.StackFunction,
) {
    val frame = cstack.popFrame()
    val depths = frame.depths
    val params = function.functionType.params.types.size
    val results = function.functionType.results.types.size
    val interfaceSlots = maxOf(params, results)
    val interfaceFramePointer = depths.values

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(
        preserveTopN = params,
        depth = interfaceFramePointer,
    )
    vstack.framePointer = interfaceFramePointer
    vstack.reserveDepth(interfaceFramePointer + interfaceSlots)

    try {
        function.body(vstack, cstack, store, context)

        val visibleResultBase = FrameSlotVisibleResultBase(frame)
        if (visibleResultBase != null) {
            repeat(results) { index ->
                val value = vstack.getFrameSlot(index)
                vstack.setFrameSlot(frame.previousFramePointer, visibleResultBase + index, value)
            }
            vstack.shrink(
                preserveTopN = 0,
                depth = depths.values,
            )
        } else {
            vstack.shrink(
                preserveTopN = 0,
                depth = interfaceFramePointer + results,
            )
        }
    } finally {
        vstack.framePointer = frame.previousFramePointer
    }
}

internal inline fun ReturnStackFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.StackFunction,
    operands: List<ControlSuperInstruction.CallOperand>,
) {
    val currentFramePointer = vstack.framePointer
    val operandValues = LongArray(operands.size) { index ->
        when (val operand = operands[index]) {
            is ControlSuperInstruction.CallOperand.Immediate -> operand.value
            is ControlSuperInstruction.CallOperand.Slot -> vstack.getFrameSlot(currentFramePointer, operand.slot)
        }
    }

    val frame = cstack.popFrame()
    val depths = frame.depths
    val results = function.functionType.results.types.size
    val interfaceSlots = maxOf(operandValues.size, results)
    val interfaceFramePointer = depths.values

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(
        preserveTopN = 0,
        depth = interfaceFramePointer,
    )
    vstack.framePointer = interfaceFramePointer
    vstack.reserveDepth(interfaceFramePointer + interfaceSlots)
    operandValues.forEachIndexed { index, value ->
        vstack.setFrameSlot(index, value)
    }

    try {
        function.body(vstack, cstack, store, context)

        val visibleResultBase = FrameSlotVisibleResultBase(frame)
        if (visibleResultBase != null) {
            repeat(results) { index ->
                val value = vstack.getFrameSlot(index)
                vstack.setFrameSlot(frame.previousFramePointer, visibleResultBase + index, value)
            }
            vstack.shrink(
                preserveTopN = 0,
                depth = depths.values,
            )
        } else {
            vstack.shrink(
                preserveTopN = 0,
                depth = interfaceFramePointer + results,
            )
        }
    } finally {
        vstack.framePointer = frame.previousFramePointer
    }
}
