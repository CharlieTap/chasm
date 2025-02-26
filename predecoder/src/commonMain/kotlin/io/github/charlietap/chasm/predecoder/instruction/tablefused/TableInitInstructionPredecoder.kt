package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableInitDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction.TableInit
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.predecoder.ext.tableAddress

internal fun TableInitInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableInit,
): Result<DispatchableInstruction, ModuleTrapError> =
    TableInitInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        dispatcher = ::TableInitDispatcher,
    )

internal inline fun TableInitInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableInit,
    crossinline loadFactory: LoadFactory,
    crossinline dispatcher: Dispatcher<TableInit>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {

    val tableAddress = context.instance.tableAddress(instruction.tableIdx).bind()
    val table = context.store.table(tableAddress)

    val elementAddress = context.instance.elementAddress(instruction.elemIdx).bind()
    val element = context.store.element(elementAddress)

    dispatcher(
        TableInit(
            elementsToInitialise = loadFactory(context, instruction.elementsToInitialise),
            segmentOffset = loadFactory(context, instruction.segmentOffset),
            tableOffset = loadFactory(context, instruction.tableOffset),
            element = element,
            table = table,
        ),
    )
}
