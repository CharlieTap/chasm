package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.atomic

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

internal inline fun AtomicMemoryInstructionPredecoder(
    context: InstantiationContext,
    instruction: AtomicMemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    Err(InstantiationError.PredecodingError).bind()
}
