package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.tableAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGrowDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.TableGrow
import io.github.charlietap.chasm.ir.instruction.TableInstruction

internal fun TableGrowInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableGrow,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableGrowInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableGrowDispatcher,
    )

internal inline fun TableGrowInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableGrow,
    crossinline dispatcher: Dispatcher<TableGrow>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address)
    val max = table.type.limits.max
        ?.toInt() ?: Int.MAX_VALUE

    dispatcher(TableGrow(table, max))
}
