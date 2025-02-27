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
        tableFillPredecoder = ::TableFillInstructionPredecoder,
        tableGetPredecoder = ::TableGetInstructionPredecoder,
        tableGrowPredecoder = ::TableGrowInstructionPredecoder,
        tableInitPredecoder = ::TableInitInstructionPredecoder,
        tableSetPredecoder = ::TableSetInstructionPredecoder,
        tableSizePredecoder = ::TableSizeInstructionPredecoder,
    )

internal inline fun FusedTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction,
    crossinline tableCopyPredecoder: Predecoder<FusedTableInstruction.TableCopy, DispatchableInstruction>,
    crossinline tableFillPredecoder: Predecoder<FusedTableInstruction.TableFill, DispatchableInstruction>,
    crossinline tableGetPredecoder: Predecoder<FusedTableInstruction.TableGet, DispatchableInstruction>,
    crossinline tableGrowPredecoder: Predecoder<FusedTableInstruction.TableGrow, DispatchableInstruction>,
    crossinline tableInitPredecoder: Predecoder<FusedTableInstruction.TableInit, DispatchableInstruction>,
    crossinline tableSetPredecoder: Predecoder<FusedTableInstruction.TableSet, DispatchableInstruction>,
    crossinline tableSizePredecoder: Predecoder<FusedTableInstruction.TableSize, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedTableInstruction.TableCopy -> tableCopyPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableFill -> tableFillPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableGet -> tableGetPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableGrow -> tableGrowPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableInit -> tableInitPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableSet -> tableSetPredecoder(context, instruction).bind()
        is FusedTableInstruction.TableSize -> tableSizePredecoder(context, instruction).bind()
    }
}
