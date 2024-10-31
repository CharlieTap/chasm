@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher

internal inline fun ReturnCallIndirectExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirect,
): Result<Unit, InvocationError> =
    CallIndirectExecutor(
        context = context,
        tableIndex = instruction.tableIndex,
        typeIndex = instruction.typeIndex,
        tailRecursion = true,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::WasmFunctionCall,
        definedTypeMatcher = ::DefinedTypeMatcher,
    )
