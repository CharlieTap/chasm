package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableCopyDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction.TableCopy

internal fun TableCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableCopy,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableCopyInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        dispatcher = ::TableCopyDispatcher,
    )

internal inline fun TableCopyInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableCopy,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<TableCopy>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val srcTableAddress = context.instance.tableAddress(instruction.srcTableIdx).bind()
    val srcTable = context.store.table(srcTableAddress)
    val dstTableAddress = context.instance.tableAddress(instruction.destTableIdx).bind()
    val dstTable = context.store.table(dstTableAddress)

    dispatcher(
        TableCopy(
            elementsToCopy = loadFactory(context, instruction.elementsToCopy),
            srcOffset = loadFactory(context, instruction.srcOffset),
            dstOffset = loadFactory(context, instruction.dstOffset),
            srcTable = srcTable,
            destTable = dstTable,
        ),
    )
}
