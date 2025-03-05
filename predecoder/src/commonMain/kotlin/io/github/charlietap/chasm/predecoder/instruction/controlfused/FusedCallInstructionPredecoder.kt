package io.github.charlietap.chasm.predecoder.instruction.controlfused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.CallDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction.WasmFunctionCall

internal fun FusedCallInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction.Call,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedCallInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        callDispatcher = ::CallDispatcher,
    )

internal inline fun FusedCallInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedControlInstruction.Call,
    crossinline loadFactory: LoadFactory,
    crossinline callDispatcher: Dispatcher<WasmFunctionCall>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.functionAddress(instruction.functionIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val inst = context.store.instruction(address)

    val operands = instruction.operands.map { operand ->
        loadFactory(context, operand)
    }

    callDispatcher(
        WasmFunctionCall(
            operands = operands,
            instruction = inst,
        ),
    )
}
