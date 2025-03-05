package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableSetDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction.TableSet

internal fun TableSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        dispatcher = ::TableSetDispatcher,
    )

internal inline fun TableSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSet,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<TableSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)

    dispatcher(
        TableSet(
            value = loadFactory(context, instruction.value),
            elementIndex = loadFactory(context, instruction.elementIdx),
            table = table,
        ),
    )
}
