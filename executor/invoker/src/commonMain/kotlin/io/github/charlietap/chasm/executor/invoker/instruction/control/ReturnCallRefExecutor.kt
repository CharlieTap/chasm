@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun ReturnCallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallRef,
): Result<Unit, InvocationError> =
    CallRefExecutor(
        context = context,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::ReturnWasmFunctionCall,
    )
