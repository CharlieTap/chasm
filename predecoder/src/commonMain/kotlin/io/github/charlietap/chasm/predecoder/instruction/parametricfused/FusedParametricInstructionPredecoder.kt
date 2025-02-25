package io.github.charlietap.chasm.predecoder.instruction.parametricfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused.SelectDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedParametricInstruction.Select
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory

internal fun FusedParametricInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedParametricInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedParametricInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        selectDispatcher = ::SelectDispatcher,
    )

internal inline fun FusedParametricInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedParametricInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline selectDispatcher: Dispatcher<Select>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedParametricInstruction.Select -> {
            selectDispatcher(
                Select(
                    const = loadFactory(context, instruction.const),
                    val1 = loadFactory(context, instruction.val1),
                    val2 = loadFactory(context, instruction.val2),
                    destination = storeFactory(context, instruction.destination),
                ),
            )
        }
    }
}
