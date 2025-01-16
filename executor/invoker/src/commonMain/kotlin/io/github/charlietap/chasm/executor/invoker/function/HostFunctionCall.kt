package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.host.HostFunctionException

internal typealias HostFunctionCall = (ExecutionContext, FunctionInstance.HostFunction) -> Unit

internal fun HostFunctionCall(
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
) {
    val (stack, store) = context
    val frame = stack.peekFrame()
    val type = function.functionType

    val params = List(type.params.types.size) {
        stack.popValue()
    }.asReversed()

    val functionContext = HostFunctionContext(
        context.config,
        store,
        frame.instance,
    )
    val results = try {
        function.function.invoke(functionContext, params)
    } catch (e: HostFunctionException) {
        throw InvocationException(InvocationError.HostFunctionError(e.reason))
    }

    results.forEach { result ->
        stack.push(result)
    }
}
