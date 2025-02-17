package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.elementAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.table.ElemDropDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction.ElemDrop
import io.github.charlietap.chasm.ir.instruction.TableInstruction

internal fun ElementDropInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.ElemDrop,
): Result<DispatchableInstruction, ModuleTrapError> =
    ElementDropInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ElemDropDispatcher,
    )

internal inline fun ElementDropInstructionPredecoder(
    context: InstantiationContext,
    instruction: TableInstruction.ElemDrop,
    crossinline dispatcher: Dispatcher<ElemDrop>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val elementAddress = context.instance?.elementAddress(instruction.elemIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val element = context.store.element(elementAddress)

    dispatcher(ElemDrop(element))
}
