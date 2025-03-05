package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableSizeDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction.TableSize

internal fun TableSizeInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSize,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableSizeInstructionPredecoder(
        context = context,
        instruction = instruction,
        storeFactory = ::StoreFactory,
        dispatcher = ::TableSizeDispatcher,
    )

internal inline fun TableSizeInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSize,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<TableSize>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)

    dispatcher(
        TableSize(
            destination = storeFactory(context, instruction.destination),
            table = table,
        ),
    )
}
