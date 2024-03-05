@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.function.FunctionCall
import io.github.charlietap.chasm.executor.invoker.function.FunctionCallImpl
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCallImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun CallExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.Call,
): Result<Unit, InvocationError> =
    CallExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        hostFunctionCall = ::HostFunctionCallImpl,
        wasmFunctionCall = ::FunctionCallImpl,
    )

internal inline fun CallExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.Call,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: FunctionCall,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrameOrError().bind()
    val address = frame.state.module.functionAddresses[instruction.functionIndex.idx.toInt()]

    when (val instance = store.function(address).bind()) {
        is FunctionInstance.HostFunction -> hostFunctionCall(store, stack, instance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(store, stack, instance).bind()
    }
}
