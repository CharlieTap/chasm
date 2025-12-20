package io.github.charlietap.chasm.predecoder.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInitDispatcher
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.TableInstruction.TableInit

internal fun TableInitInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableInit,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableInitInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::TableInitDispatcher,
    )

internal inline fun TableInitInstructionPredecoder(
    context: PredecodingContext,
    instruction: TableInstruction.TableInit,
    crossinline dispatcher: Dispatcher<TableInit>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val tableAddress = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(tableAddress)

    val elementAddress = context.instance.elementAddress(instruction.elemIdx).bind()
    val element = context.store.element(elementAddress)

    dispatcher(TableInit(element, table))
}
