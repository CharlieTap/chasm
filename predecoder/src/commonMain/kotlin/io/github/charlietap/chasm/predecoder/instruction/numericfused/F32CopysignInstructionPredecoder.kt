package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32CopysignDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F32Copysign

internal fun F32CopysignInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F32Copysign,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    F32CopysignInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::F32CopysignDispatcher,
    )

internal inline fun F32CopysignInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F32Copysign,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<F32Copysign>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val left = loadFactory(context, instruction.left)
    val right = loadFactory(context, instruction.right)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        F32Copysign(
            left = left,
            right = right,
            destination = destination,
        ),
    )
}
