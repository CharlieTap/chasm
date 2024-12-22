package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalSetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.ext.globalAddress
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.GlobalSet

internal fun GlobalSetInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction.GlobalSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    GlobalSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::GlobalSetDispatcher,
    )

internal inline fun GlobalSetInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction.GlobalSet,
    crossinline dispatcher: Dispatcher<GlobalSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.globalAddress(instruction.globalIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val global = context.store.global(address).bind()

    dispatcher(GlobalSet(global))
}
