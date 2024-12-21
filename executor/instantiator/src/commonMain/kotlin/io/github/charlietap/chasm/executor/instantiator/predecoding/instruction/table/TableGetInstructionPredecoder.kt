package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableGetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.TableGet

internal fun TableGetInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableGetDispatcher,
    )

internal inline fun TableGetInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableGet,
    crossinline dispatcher: Dispatcher<TableGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address).bind()

    dispatcher(TableGet(table))
}