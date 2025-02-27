package io.github.charlietap.chasm.predecoder.instruction.referencefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefNullDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction.RefNull
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory

internal fun FusedReferenceInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedReferenceInstructionPredecoder(
        context = context,
        instruction = instruction,
        storeFactory = ::StoreFactory,
        refNullDispatcher = ::RefNullDispatcher,
    )

internal inline fun FusedReferenceInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction,
    crossinline storeFactory: StoreFactory,
    crossinline refNullDispatcher: Dispatcher<RefNull>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedReferenceInstruction.RefNull -> {
            val destination = storeFactory(context, instruction.destination)
            val reference = ReferenceValue.Null(instruction.type).toLong()
            refNullDispatcher(RefNull(destination, reference))
        }
    }
}
