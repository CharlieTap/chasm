package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError

internal inline fun VectorInstructionPredecoder(
    context: InstantiationContext,
    instruction: VectorInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    Err(InstantiationError.PredecodingError).bind()
}
