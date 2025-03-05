package io.github.charlietap.chasm.predecoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableCopyDispatcher
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.TableInstruction.TableCopy

internal fun TableCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableCopy,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableCopyInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableCopyDispatcher,
    )

internal inline fun TableCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableCopy,
    crossinline dispatcher: Dispatcher<TableCopy>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val srcTableAddress = context.instance.tableAddress(instruction.srcTableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val srcTable = context.store.table(srcTableAddress)
    val dstTableAddress = context.instance.tableAddress(instruction.destTableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val dstTable = context.store.table(dstTableAddress)

    dispatcher(TableCopy(srcTable, dstTable))
}
