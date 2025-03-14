package io.github.charlietap.chasm.predecoder.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalGetDispatcher
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.globalAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction.GlobalGet

internal fun GlobalGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction.GlobalGet,
): Result<DispatchableInstruction, ModuleTrapError> =
    GlobalGetInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::GlobalGetDispatcher,
    )

internal inline fun GlobalGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction.GlobalGet,
    crossinline dispatcher: Dispatcher<GlobalGet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.globalAddress(instruction.globalIdx)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val global = context.store.global(address)

    dispatcher(GlobalGet(global))
}
