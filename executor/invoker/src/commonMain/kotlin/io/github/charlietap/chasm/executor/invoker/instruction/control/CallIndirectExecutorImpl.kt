@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.invoker.function.FunctionCall
import io.github.charlietap.chasm.executor.invoker.function.FunctionCallImpl
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCallImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun CallIndirectExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.CallIndirect,
): Result<Unit, InvocationError> =
    CallIndirectExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        hostFunctionCall = ::HostFunctionCallImpl,
        wasmFunctionCall = ::FunctionCallImpl,
    )

internal inline fun CallIndirectExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: ControlInstruction.CallIndirect,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: FunctionCall,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrameOrError().bind()

    val tableAddress = frame.state.module.tableAddress(instruction.tableIndex.index()).bind()
    val tableInstance = store.table(tableAddress).bind()

    val functionType = frame.state.module.functionType(instruction.typeIndex.index()).bind()

    val elementIndex = stack.popI32().bind()
    val reference = tableInstance.elements.getOrNull(elementIndex).toResultOr {
        InvocationError.TableElementLookupFailed(elementIndex)
    }.bind()

    val address = when (reference) {
        is ReferenceValue.FunctionAddress -> Ok(reference.address)
        else -> Err(InvocationError.IndirectCallOnANonFunctionReference)
    }.bind()

    val functionInstance = store.function(address).bind()

    if (functionInstance.type != functionType) {
        Err(InvocationError.IndirectCallHasIncorrectFunctionType).bind<Unit>()
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(store, stack, functionInstance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(store, stack, functionInstance).bind()
    }
}
