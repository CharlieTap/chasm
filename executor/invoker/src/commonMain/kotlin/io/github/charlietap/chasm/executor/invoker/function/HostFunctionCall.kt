package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.OperandCopyPlan
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.NO_RESULT_SLOT_BASE
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
    val type = function.functionType

    val params = List(type.params.types.size) {
        vstack.pop()
    }.asReversed()

    val functionContext = HostFunctionContext(
        context.config,
        store,
        cstack.frameInstance(),
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
    resultSlotBase: Int,
    callFrameSlot: Int,
) {
    val functionContext = HostFunctionContext(
        context.config,
        store,
        cstack.frameInstance(),
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
        vstack.setFrameSlot(resultSlotBase + index, result.toLongFromBoxed())
    }
}

internal fun HostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
    operands: OperandCopyPlan,
    resultSlotBase: Int,
) {
    val functionContext = HostFunctionContext(
        context.config,
        store,
        cstack.frameInstance(),
    )
    val results = try {
        val framePointer = vstack.framePointer
        val hostParams = function.functionType.params.types.mapIndexed { idx, expected ->
            val value = operandValue(vstack, framePointer, operands.operands[idx])
            value.toExecutionValue(expected)
        }
        function.function.invoke(functionContext, hostParams)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    results.forEachIndexed { index, result ->
        vstack.setFrameSlot(resultSlotBase + index, result.toLongFromBoxed())
    }
}

internal fun ReturnHostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
): Int {
    val params = List(function.functionType.params.types.size) {
        vstack.pop()
    }.asReversed()
    val handlerDepth = cstack.frameHandlerDepth()
    val valueDepth = cstack.frameValueDepth()
    val instance = cstack.frameInstance()
    val previousFramePointer = cstack.framePreviousFramePointer()
    val resultSlotBase = cstack.frameResultSlotBase()
    val returnIp = cstack.frameReturnIp()
    cstack.discardFrame()
    cstack.shrinkHandlers(handlerDepth)
    vstack.shrink(0, valueDepth)
    vstack.framePointer = previousFramePointer

    val functionContext = HostFunctionContext(context.config, store, instance)
    val results = try {
        val hostParams = params.mapIndexed { index, param ->
            param.toExecutionValue(function.functionType.params.types[index])
        }
        function.function.invoke(functionContext, hostParams)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    if (resultSlotBase != NO_RESULT_SLOT_BASE) {
        results.forEachIndexed { index, result ->
            vstack.setFrameSlot(resultSlotBase + index, result.toLongFromBoxed())
        }
    } else {
        results.forEach { result ->
            vstack.push(result.toLongFromBoxed())
        }
    }
    return returnIp
}

internal fun ReturnHostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
    operands: List<CopyOperand>,
): Int {
    val currentFramePointer = vstack.framePointer
    val operandValues = LongArray(operands.size) { index ->
        when (val operand = operands[index]) {
            is CopyOperand.Immediate -> operand.value
            is CopyOperand.Slot -> vstack.getFrameSlot(currentFramePointer, operand.slot)
        }
    }

    val handlerDepth = cstack.frameHandlerDepth()
    val valueDepth = cstack.frameValueDepth()
    val instance = cstack.frameInstance()
    val previousFramePointer = cstack.framePreviousFramePointer()
    val resultSlotBase = cstack.frameResultSlotBase()
    val returnIp = cstack.frameReturnIp()
    cstack.discardFrame()
    cstack.shrinkHandlers(handlerDepth)
    vstack.shrink(0, valueDepth)
    vstack.framePointer = previousFramePointer

    val functionContext = HostFunctionContext(
        context.config,
        store,
        instance,
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

    if (resultSlotBase != NO_RESULT_SLOT_BASE) {
        results.forEachIndexed { index, result ->
            vstack.setFrameSlot(resultSlotBase + index, result.toLongFromBoxed())
        }
    } else {
        results.forEach { result ->
            vstack.push(result.toLongFromBoxed())
        }
    }
    return returnIp
}
