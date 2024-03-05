package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

@Suppress("UNUSED_PARAMETER")
internal fun HostFunctionCallImpl(
    store: Store,
    stack: Stack,
    function: FunctionInstance.HostFunction,
): Result<Unit, InvocationError> = binding {
    TODO()
}
