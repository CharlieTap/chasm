package io.github.charlietap.chasm.predecoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSetDispatcher
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.TableInstruction.TableSet

internal fun TableSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableSetDispatcher,
    )

internal inline fun TableSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableSet,
    crossinline dispatcher: Dispatcher<TableSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address)

    dispatcher(TableSet(table))
}
