package io.github.charlietap.chasm.predecoder.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun TableInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableInstructionPredecoder(
        context = context,
        instruction = instruction,
        tableGetPredecoder = ::TableGetInstructionPredecoder,
        tableSetPredecoder = ::TableSetInstructionPredecoder,
        tableInitPredecoder = ::TableInitInstructionPredecoder,
        elemDropPredecoder = ::ElementDropInstructionPredecoder,
        tableCopyPredecoder = ::TableCopyInstructionPredecoder,
        tableGrowPredecoder = ::TableGrowInstructionPredecoder,
        tableSizePredecoder = ::TableSizeInstructionPredecoder,
        tableFillPredecoder = ::TableFillInstructionPredecoder,
    )

internal inline fun TableInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction,
    crossinline tableGetPredecoder: Predecoder<TableInstruction.TableGet, DispatchableInstruction>,
    crossinline tableSetPredecoder: Predecoder<TableInstruction.TableSet, DispatchableInstruction>,
    crossinline tableInitPredecoder: Predecoder<TableInstruction.TableInit, DispatchableInstruction>,
    crossinline elemDropPredecoder: Predecoder<TableInstruction.ElemDrop, DispatchableInstruction>,
    crossinline tableCopyPredecoder: Predecoder<TableInstruction.TableCopy, DispatchableInstruction>,
    crossinline tableGrowPredecoder: Predecoder<TableInstruction.TableGrow, DispatchableInstruction>,
    crossinline tableSizePredecoder: Predecoder<TableInstruction.TableSize, DispatchableInstruction>,
    crossinline tableFillPredecoder: Predecoder<TableInstruction.TableFill, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is TableInstruction.TableGet -> tableGetPredecoder(context, instruction).bind()
        is TableInstruction.TableSet -> tableSetPredecoder(context, instruction).bind()
        is TableInstruction.TableInit -> tableInitPredecoder(context, instruction).bind()
        is TableInstruction.ElemDrop -> elemDropPredecoder(context, instruction).bind()
        is TableInstruction.TableCopy -> tableCopyPredecoder(context, instruction).bind()
        is TableInstruction.TableGrow -> tableGrowPredecoder(context, instruction).bind()
        is TableInstruction.TableSize -> tableSizePredecoder(context, instruction).bind()
        is TableInstruction.TableFill -> tableFillPredecoder(context, instruction).bind()
    }
}
