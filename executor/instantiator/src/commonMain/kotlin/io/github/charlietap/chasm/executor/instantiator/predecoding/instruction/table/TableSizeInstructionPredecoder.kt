package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableSizeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.TableSize

internal fun TableSizeInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableSize,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableSizeInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableSizeDispatcher,
    )

internal inline fun TableSizeInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableSize,
    crossinline dispatcher: Dispatcher<TableSize>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address).bind()

    dispatcher(TableSize(table))
}
