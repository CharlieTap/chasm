package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.control.HostFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmFunctionCallDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.HostFunctionCall
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.WasmFunctionCall

internal inline fun CallInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Call,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.functionAddress(instruction.functionIndex).bind()
    when (val instance = context.store.function(address)) {
        is FunctionInstance.HostFunction -> HostFunctionCallDispatcher(HostFunctionCall(instance))
        is FunctionInstance.WasmFunction -> WasmFunctionCallDispatcher(WasmFunctionCall(instance))
    }
}
