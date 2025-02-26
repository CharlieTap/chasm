package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableGrowDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction.TableGrow
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.predecoder.ext.tableAddress

internal fun TableGrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGrow,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableGrowInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        dispatcher = ::TableGrowDispatcher,
    )

internal inline fun TableGrowInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGrow,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<TableGrow>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)
    val max = table.type.limits.max?.toInt() ?: Int.MAX_VALUE

    dispatcher(
        TableGrow(
            elementsToAdd = loadFactory(context, instruction.elementsToAdd),
            referenceValue = loadFactory(context, instruction.referenceValue),
            destination = storeFactory(context, instruction.destination),
            table = table,
            max = max,
        ),
    )
}
