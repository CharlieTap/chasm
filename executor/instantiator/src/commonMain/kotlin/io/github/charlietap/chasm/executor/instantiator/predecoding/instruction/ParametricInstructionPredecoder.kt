package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.DropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.SelectWithTypeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction.Drop
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction.Select
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction.SelectWithType

internal fun ParametricInstructionPredecoder(
    context: InstantiationContext,
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
    context: InstantiationContext,
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
