package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun CallRefExecutor(
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
        is FunctionInstance.HostFunction -> hostFunctionCall(context, instance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, instance).bind()
    }
}
