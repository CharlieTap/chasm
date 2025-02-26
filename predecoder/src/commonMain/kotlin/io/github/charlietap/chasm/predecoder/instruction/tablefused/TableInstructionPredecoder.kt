package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext

internal fun FusedTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedTableInstructionPredecoder(
        context = context,
        instruction = instruction,
        tableCopyPredecoder = ::TableCopyInstructionPredecoder,
    )

internal inline fun FusedTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction,
    crossinline tableCopyPredecoder: Predecoder<FusedTableInstruction.TableCopy, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedTableInstruction.TableCopy -> tableCopyPredecoder(context, instruction).bind()
    }
}
