package io.github.charlietap.chasm.predecoder.instruction.numericfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64CopysignDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction.F64Copysign

internal fun F64CopysignInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F64Copysign,
    loadFactory: LoadFactory = ::LoadFactory,
    storeFactory: StoreFactory = ::StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> =
    F64CopysignInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = loadFactory,
        storeFactory = storeFactory,
        dispatcher = ::F64CopysignDispatcher,
    )

internal inline fun F64CopysignInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedNumericInstruction.F64Copysign,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline dispatcher: Dispatcher<F64Copysign>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val left = loadFactory(context, instruction.left)
    val right = loadFactory(context, instruction.right)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(
        F64Copysign(
            left = left,
            right = right,
            destination = destination,
        ),
    )
}
