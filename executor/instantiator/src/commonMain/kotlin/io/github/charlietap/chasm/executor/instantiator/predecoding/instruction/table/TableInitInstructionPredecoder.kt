package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.elementAddress
import io.github.charlietap.chasm.executor.instantiator.ext.tableAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.TableInit
import io.github.charlietap.chasm.ir.instruction.TableInstruction

internal fun TableInitInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableInit,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableInitInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableInitDispatcher,
    )

internal inline fun TableInitInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.TableInit,
    crossinline dispatcher: Dispatcher<TableInit>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val tableAddress = context.instance?.tableAddress(instruction.tableIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(tableAddress)

    val elementAddress = context.instance?.elementAddress(instruction.elemIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val element = context.store.element(elementAddress)

    dispatcher(TableInit(element, table))
}
