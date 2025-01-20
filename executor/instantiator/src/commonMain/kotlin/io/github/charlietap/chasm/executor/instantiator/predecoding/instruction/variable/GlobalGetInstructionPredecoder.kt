package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.globalAddress
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalGetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.GlobalGet

internal fun GlobalGetInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction.GlobalGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    GlobalGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::GlobalGetDispatcher,
    )

internal inline fun GlobalGetInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction.GlobalGet,
    crossinline dispatcher: Dispatcher<GlobalGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.globalAddress(instruction.globalIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val global = context.store.global(address)

    dispatcher(GlobalGet(global))
}
