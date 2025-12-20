package io.github.charlietap.chasm.predecoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableFillDispatcher
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.TableInstruction.TableFill

internal fun TableFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableFill,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableFillInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableFillDispatcher,
    )

internal inline fun TableFillInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableFill,
    crossinline dispatcher: Dispatcher<TableFill>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(address)

    dispatcher(TableFill(table))
}
