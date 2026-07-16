package io.github.charlietap.chasm.embedding.transform

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunction
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction

internal fun ComponentHostFunctionMapper(
    store: Store,
    function: ComponentHostFunction,
): RuntimeComponentHostFunction {
    val hostContext = ComponentHostFunctionContext(store)
    return RuntimeComponentHostFunction.Dynamic { context, arguments ->
        if (store.identity !== context.store) {
            Err(ComponentInvocationError.StoreMismatch)
        } else {
            try {
                val results = with(hostContext) {
                    function(arguments)
                }
                Ok(results)
            } catch (exception: HostFunctionException) {
                Err(ComponentInvocationError.HostFunctionFailure(exception.reason))
            }
        }
    }
}
