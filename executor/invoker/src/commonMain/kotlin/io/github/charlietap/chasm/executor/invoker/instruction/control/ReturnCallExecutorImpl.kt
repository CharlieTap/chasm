@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCallImpl
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCallImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun ReturnCallExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.ReturnCall,
): Result<Unit, InvocationError> =
    CallExecutorImpl(
        store = store,
        stack = stack,
        functionIndex = instruction.functionIndex,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCallImpl,
        wasmFunctionCall = ::WasmFunctionCallImpl,
    )
