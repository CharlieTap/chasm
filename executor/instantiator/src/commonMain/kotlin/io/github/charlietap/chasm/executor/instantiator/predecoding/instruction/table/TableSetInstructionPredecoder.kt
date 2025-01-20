package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.tableAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.TableSet

internal fun TableSetInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableSetDispatcher,
    )

internal inline fun TableSetInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableSet,
    crossinline dispatcher: Dispatcher<TableSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address)

    dispatcher(TableSet(table))
}
