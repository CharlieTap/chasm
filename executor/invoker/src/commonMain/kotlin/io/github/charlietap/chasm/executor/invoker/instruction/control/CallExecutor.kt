package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance

internal inline fun CallExecutor(
    context: ExecutionContext,
    functionIndex: Index.FunctionIndex,
    tailRecursion: Boolean,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val address = frame.state.module.functionAddresses[functionIndex.index()]

    when (val instance = store.function(address).bind()) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, instance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, instance).bind()
    }
}
