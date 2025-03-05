package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnHostFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnWasmFunctionCallDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnHostFunctionCall
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnWasmFunctionCall

internal fun ReturnCallInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCall,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReturnCallInstructionPredecoder(
        context = context,
        instruction = instruction,
        returnWasmFunctionCallDispatcher = ::ReturnWasmFunctionCallDispatcher,
        returnHostFunctionCallDispatcher = ::ReturnHostFunctionCallDispatcher,
    )

internal inline fun ReturnCallInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCall,
    crossinline returnWasmFunctionCallDispatcher: Dispatcher<ReturnWasmFunctionCall>,
    crossinline returnHostFunctionCallDispatcher: Dispatcher<ReturnHostFunctionCall>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.functionAddress(instruction.functionIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val instance = context.store.function(address)
    when (instance) {
        is FunctionInstance.HostFunction -> {
            returnHostFunctionCallDispatcher(ReturnHostFunctionCall(instance))
        }
        is FunctionInstance.WasmFunction -> {
            returnWasmFunctionCallDispatcher(ReturnWasmFunctionCall(instance))
        }
    }
}
