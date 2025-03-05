package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableGetDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction.TableGet

internal fun TableGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        dispatcher = ::TableGetDispatcher,
    )

internal inline fun TableGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGet,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<TableGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)

    dispatcher(
        TableGet(
            elementIndex = loadFactory(context, instruction.elementIndex),
            destination = storeFactory(context, instruction.destination),
            table = table,
        ),
    )
}
