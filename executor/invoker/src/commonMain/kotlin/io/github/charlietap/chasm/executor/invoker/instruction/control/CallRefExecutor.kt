@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance

internal inline fun CallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
): Result<Unit, InvocationError> =
    CallRefExecutor(
        context = context,
        tailRecursion = false,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::WasmFunctionCall,
    )

internal inline fun CallRefExecutor(
    context: ExecutionContext,
    tailRecursion: Boolean,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val value = stack.popFunctionAddress().bind()

    when (val instance = store.function(value.address).bind()) {
        is FunctionInstance.HostFunction -> hostFunctionCall(store, stack, instance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(store, stack, instance, tailRecursion).bind()
    }
}
