@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ReturnCallRefExecutor = (Store, Stack) -> Result<Unit, InvocationError>

internal inline fun ReturnCallRefExecutor(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    CallRefExecutor(
        store = store,
        stack = stack,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::WasmFunctionCall,
    )
