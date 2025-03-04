package io.github.charlietap.chasm.predecoder.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.DropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectWithTypeDispatcher
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction.Drop
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction.Select
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction.SelectWithType

internal fun ParametricInstructionPredecoder(
    context: PredecodingContext,
    instruction: ParametricInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    ParametricInstructionPredecoder(
        context = context,
        instruction = instruction,
        dropDispatcher = ::DropDispatcher,
        selectDispatcher = ::SelectDispatcher,
        selectWithTypeDispatcher = ::SelectWithTypeDispatcher,
    )

internal inline fun ParametricInstructionPredecoder(
    context: PredecodingContext,
    instruction: ParametricInstruction,
    crossinline dropDispatcher: Dispatcher<Drop>,
    crossinline selectDispatcher: Dispatcher<Select>,
    crossinline selectWithTypeDispatcher: Dispatcher<SelectWithType>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is ParametricInstruction.Drop -> dropDispatcher(Drop)
        is ParametricInstruction.Select -> selectDispatcher(Select)
        is ParametricInstruction.SelectWithType -> selectWithTypeDispatcher(
            SelectWithType(instruction.types),
        )
    }
}
