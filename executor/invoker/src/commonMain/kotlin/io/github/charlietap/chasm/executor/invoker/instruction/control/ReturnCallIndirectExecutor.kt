package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher

internal inline fun ReturnCallIndirectExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirect,
): Result<Unit, InvocationError> =
    CallIndirectExecutor(
        context = context,
        table = instruction.table,
        typeIndex = instruction.typeIndex,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::ReturnWasmFunctionCall,
        definedTypeMatcher = ::DefinedTypeMatcher,
    )
