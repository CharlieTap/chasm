package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.BrTableDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.BrTable

internal fun BrTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrTable,
): Result<DispatchableInstruction, ModuleTrapError> =
    BrTableInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::BrTableDispatcher,
    )

internal inline fun BrTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.BrTable,
    crossinline dispatcher: Dispatcher<BrTable>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(
        BrTable(
            labelIndices = instruction.labelIndices,
            defaultLabelIndex = instruction.defaultLabelIndex,
        ),
    )
} 
