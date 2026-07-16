package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.executor.invoker.function.StackFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun NestedFunctionInvoker(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    execution: ExecutionContext,
    instance: ModuleInstance,
    address: Address.Function,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
): Int {
    val function = store.function(address)
    val resultCount = function.functionType.results.types.size
    if (argumentCount != function.functionType.params.types.size) {
        throw InvocationException(InvocationError.FunctionInconsistentWithType)
    }
    if (results.size < resultCount) {
        throw InvocationException(InvocationError.ProgramFinishedInconsistentState)
    }

    return when (function) {
        is FunctionInstance.HostFunction -> RawFunctionInvoker(
            execution.config,
            store,
            instance,
            address,
            arguments,
            argumentCount,
            results,
        ).getOrThrow()
        is FunctionInstance.StackFunction -> nestedExecution(cstack) {
            invokeStackFunction(
                vstack,
                cstack,
                store,
                execution,
                function,
                arguments,
                argumentCount,
                results,
                resultCount,
            )
        }
        is FunctionInstance.WasmFunction -> nestedExecution(cstack) {
            invokeWasmFunction(
                vstack,
                cstack,
                store,
                execution,
                function,
                arguments,
                argumentCount,
                results,
                resultCount,
            )
        }
    }
}

private inline fun <T> nestedExecution(
    cstack: ControlStack,
    block: () -> T,
): T {
    cstack.enterNestedExecution()
    return try {
        block()
    } finally {
        cstack.leaveNestedExecution()
    }
}

private fun invokeStackFunction(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    execution: ExecutionContext,
    function: FunctionInstance.StackFunction,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
    resultCount: Int,
): Int {
    val depth = vstack.depth()
    vstack.push(arguments, argumentCount)
    return try {
        StackFunctionCall(vstack, cstack, store, execution, function)
        copyResults(vstack, depth, results, resultCount)
    } finally {
        vstack.shrink(preserveTopN = 0, depth = depth)
    }
}

private fun invokeWasmFunction(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    execution: ExecutionContext,
    function: FunctionInstance.WasmFunction,
    arguments: LongArray,
    argumentCount: Int,
    results: LongArray,
    resultCount: Int,
): Int {
    val valuesDepth = vstack.depth()
    val framePointer = vstack.framePointer
    val frameDepth = cstack.framesDepth()
    val handlerDepth = cstack.handlersDepth()
    val instructionDepth = cstack.instructionsDepth()
    val labelDepth = cstack.labelsDepth()

    vstack.push(arguments, argumentCount)
    return try {
        WasmFunctionCall(vstack, cstack, store, execution, function)
        cstack.instructionStack().executeUntil(instructionDepth, vstack, cstack, store, execution)
        copyResults(vstack, valuesDepth, results, resultCount)
    } finally {
        cstack.shrinkFrames(frameDepth)
        cstack.shrinkHandlers(handlerDepth)
        cstack.shrinkInstructions(instructionDepth)
        cstack.shrinkLabels(labelDepth)
        vstack.shrink(preserveTopN = 0, depth = valuesDepth)
        vstack.framePointer = framePointer
    }
}

private fun copyResults(
    vstack: ValueStack,
    valuesDepth: Int,
    results: LongArray,
    resultCount: Int,
): Int {
    if (vstack.depth() != valuesDepth + resultCount) {
        throw InvocationException(InvocationError.ProgramFinishedInconsistentState)
    }
    repeat(resultCount) { index -> results[index] = vstack.getFrameSlot(valuesDepth, index) }
    return resultCount
}

private fun <T> com.github.michaelbull.result.Result<T, InvocationError>.getOrThrow(): T = fold(
    success = { it },
    failure = { throw InvocationException(it) },
)
