package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.HostFunctionCallDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmFunctionCallDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.functionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.HostFunctionCall
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction.WasmFunctionCall

internal fun CallInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.Call,
): Result<DispatchableInstruction, ModuleTrapError> =
    CallInstructionPredecoder(
        context = context,
        instruction = instruction,
        wasmFunctionCallDispatcher = ::WasmFunctionCallDispatcher,
        hostFunctionCallDispatcher = ::HostFunctionCallDispatcher,
    )

internal inline fun CallInstructionPredecoder(
    context: InstantiationContext,
    instruction: ControlInstruction.Call,
    crossinline wasmFunctionCallDispatcher: Dispatcher<WasmFunctionCall>,
    crossinline hostFunctionCallDispatcher: Dispatcher<HostFunctionCall>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance?.functionAddress(instruction.functionIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val instance = context.store.function(address).bind()
    when (instance) {
        is FunctionInstance.HostFunction -> {
            hostFunctionCallDispatcher(HostFunctionCall(instance))
        }
        is FunctionInstance.WasmFunction -> {
            wasmFunctionCallDispatcher(WasmFunctionCall(instance))
        }
    }
}
