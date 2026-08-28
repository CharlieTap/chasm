package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias HostFunctionCall = (ValueStack, ControlStack, Store, ExecutionContext, FunctionInstance.HostFunction) -> Unit

@OptIn(UnsafeHostApi::class)
internal fun HostFunctionCall(
    vstack: ValueStack,
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance.HostFunction,
    parameterSlotBase: Int,
    resultSlotBase: Int,
) {
    val framePointer = vstack.framePointer
    function.function.invokeHost(
        stack = vstack.unsafeElements(),
        parameterBase = framePointer + parameterSlotBase,
        resultBase = framePointer + resultSlotBase,
        caller = caller,
        context = context,
    )
}

@OptIn(UnsafeHostApi::class)
internal fun HostFunctionCall(
    vstack: ValueStack,
    context: ExecutionContext,
    caller: ModuleInstance,
    function: FunctionInstance.HostFunction,
) {
    val parameterCount = function.functionType.params.types.size
    val resultCount = function.functionType.results.types.size
    val parameterBase = vstack.depth() - parameterCount
    val resultEnd = parameterBase + resultCount
    vstack.reserveDepth(resultEnd)
    function.function.invokeHost(
        stack = vstack.unsafeElements(),
        parameterBase = parameterBase,
        resultBase = parameterBase,
        caller = caller,
        context = context,
    )
    vstack.shrink(preserveTopN = 0, depth = resultEnd)
}

internal fun HostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
) {
    HostFunctionCall(vstack, context, cstack.frameInstance(), function)
}

internal inline fun HostFunction.invokeHost(
    stack: LongArray,
    parameterBase: Int,
    resultBase: Int,
    caller: ModuleInstance,
    context: ExecutionContext,
) {
    try {
        context.withHostCallbackScope {
            context(stack, caller, context) {
                invoke(parameterBase, resultBase)
            }
        }
    } catch (exception: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(exception.reason))
    }
}
