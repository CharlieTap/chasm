@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun ReturnCallExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCall,
): Result<Unit, InvocationError> =
    CallExecutor(
        context = context,
        functionIndex = instruction.functionIndex,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::ReturnWasmFunctionCall,
    )
