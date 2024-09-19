@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ReturnCallExecutor = (Store, Stack, ControlInstruction.ReturnCall) -> Result<Unit, InvocationError>

internal inline fun ReturnCallExecutor(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.ReturnCall,
): Result<Unit, InvocationError> =
    CallExecutor(
        store = store,
        stack = stack,
        functionIndex = instruction.functionIndex,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::WasmFunctionCall,
    )
