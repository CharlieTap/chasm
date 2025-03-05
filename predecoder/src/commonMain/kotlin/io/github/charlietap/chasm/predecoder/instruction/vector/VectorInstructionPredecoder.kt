package io.github.charlietap.chasm.predecoder.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.VectorInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal inline fun VectorInstructionPredecoder(
    context: PredecodingContext,
    instruction: VectorInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    Err(InstantiationError.PredecodingError).bind()
}
