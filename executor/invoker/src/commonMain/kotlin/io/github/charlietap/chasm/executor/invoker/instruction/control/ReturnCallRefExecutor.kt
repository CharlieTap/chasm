package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun ReturnCallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallRef,
) = ReturnCallRefExecutor(
    context = context,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::ReturnWasmFunctionCall,
)

internal inline fun ReturnCallRefExecutor(
    context: ExecutionContext,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
) {
    val stack = context.vstack
    val store = context.store
    val address = stack.popFunctionAddress()

    when (val instance = store.function(address)) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, instance)
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, instance)
    }
}
