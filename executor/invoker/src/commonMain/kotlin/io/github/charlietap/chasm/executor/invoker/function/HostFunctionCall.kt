package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias HostFunctionCall = (ValueStack, ControlStack, Store, ExecutionContext, FunctionInstance.HostFunction) -> Unit

internal fun HostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
) {
    val frame = cstack.peekFrame()
    val type = function.functionType

    val params = List(type.params.types.size) {
        vstack.pop()
    }.asReversed()

    val functionContext = HostFunctionContext(
        context.config,
        store,
        frame.instance,
    )
    val results = try {
        val hostParams = params.mapIndexed { idx, param ->
            val expected = function.functionType.params.types
                .getOrNull(idx)
            if (expected == null) {
                throw InvocationException(InvocationError.FunctionInconsistentWithType)
            }
            param.toExecutionValue(expected)
        }
        function.function.invoke(functionContext, hostParams)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    results.forEach { result ->
        vstack.push(result.toLongFromBoxed())
    }
}

internal fun HostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
    resultSlots: List<Int>,
    callFrameSlot: Int,
) {
    val frame = cstack.peekFrame()

    val functionContext = HostFunctionContext(
        context.config,
        store,
        frame.instance,
    )
    val results = try {
        val hostParams = function.functionType.params.types.mapIndexed { idx, expected ->
            val value = vstack.getFrameSlot(callFrameSlot + idx)
            value.toExecutionValue(expected)
        }
        function.function.invoke(functionContext, hostParams)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    results.forEachIndexed { index, result ->
        vstack.setFrameSlot(resultSlots[index], result.toLongFromBoxed())
    }
}

internal fun ReturnHostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
    operands: List<FusedControlInstruction.CallOperand>,
) {
    val currentFramePointer = vstack.framePointer
    val operandValues = LongArray(operands.size) { index ->
        when (val operand = operands[index]) {
            is FusedControlInstruction.CallOperand.Immediate -> operand.value
            is FusedControlInstruction.CallOperand.Slot -> vstack.getFrameSlot(currentFramePointer, operand.slot)
        }
    }

    val frame = cstack.popFrame()
    val depths = frame.depths
    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels + 1)
    vstack.shrink(0, depths.values)
    vstack.framePointer = frame.previousFramePointer

    val functionContext = HostFunctionContext(
        context.config,
        store,
        frame.instance,
    )
    val results = try {
        val hostParams = operandValues.mapIndexed { idx, value ->
            val expected = function.functionType.params.types
                .getOrNull(idx)
            if (expected == null) {
                throw InvocationException(InvocationError.FunctionInconsistentWithType)
            }

            value.toExecutionValue(expected)
        }
        function.function.invoke(functionContext, hostParams)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    val visibleResultBase = FrameSlotVisibleResultBase(frame)
    if (visibleResultBase != null) {
        results.forEachIndexed { index, result ->
            vstack.setFrameSlot(visibleResultBase + index, result.toLongFromBoxed())
        }
    } else {
        results.forEach { result ->
            vstack.push(result.toLongFromBoxed())
        }
    }
}
