package io.github.charlietap.chasm.predecoder.instruction.atomic

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal inline fun AtomicMemoryInstructionPredecoder(
    context: PredecodingContext,
    instruction: AtomicMemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    Err(InstantiationError.PredecodingError).bind()
}
