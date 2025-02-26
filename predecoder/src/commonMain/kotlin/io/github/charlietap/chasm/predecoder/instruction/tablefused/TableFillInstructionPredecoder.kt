package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableFillDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction.TableFill
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress

internal fun TableFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableFill,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        dispatcher = ::TableFillDispatcher,
    )

internal inline fun TableFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableFill,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<TableFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)

    dispatcher(
        TableFill(
            elementsToFill = loadFactory(context, instruction.elementsToFill),
            fillValue = loadFactory(context, instruction.fillValue),
            tableOffset = loadFactory(context, instruction.tableOffset),
            table = table,
        ),
    )
}
