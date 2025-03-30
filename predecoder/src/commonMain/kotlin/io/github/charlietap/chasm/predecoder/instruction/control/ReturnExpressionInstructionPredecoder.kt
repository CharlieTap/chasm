package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnExpressionDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnExpression

internal fun ReturnExpressionInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnExpression,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReturnExpressionInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ReturnExpressionDispatcher,
    )

internal inline fun ReturnExpressionInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnExpression,
    crossinline dispatcher: Dispatcher<ReturnExpression>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    dispatcher(ReturnExpression)
}
