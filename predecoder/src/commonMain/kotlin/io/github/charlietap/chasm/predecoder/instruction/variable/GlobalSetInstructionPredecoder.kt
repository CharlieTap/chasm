package io.github.charlietap.chasm.predecoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalSetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.GlobalSet
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.globalAddress

internal fun GlobalSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction.GlobalSet,
): Result<DispatchableInstruction, ModuleTrapError> =
    GlobalSetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::GlobalSetDispatcher,
    )

internal inline fun GlobalSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction.GlobalSet,
    crossinline dispatcher: Dispatcher<GlobalSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.globalAddress(instruction.globalIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val global = context.store.global(address)

    dispatcher(GlobalSet(global))
}
