package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun ReturnCallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallRef,
): Result<Unit, InvocationError> =
    ReturnCallRefExecutor(
        context = context,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::ReturnWasmFunctionCall,
    )

internal inline fun ReturnCallRefExecutor(
    context: ExecutionContext,
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
