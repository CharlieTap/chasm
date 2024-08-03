@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCallImpl
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCallImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
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
        tableIndex = instruction.tableIndex,
        typeIndex = instruction.typeIndex,
        tailRecursion = false,
        hostFunctionCall = ::HostFunctionCallImpl,
        wasmFunctionCall = ::WasmFunctionCallImpl,
    )

internal inline fun CallIndirectExecutorImpl(
    store: Store,
    stack: Stack,
    tableIndex: Index.TableIndex,
    typeIndex: Index.TypeIndex,
    tailRecursion: Boolean,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()

    val tableAddress = frame.state.module.tableAddress(tableIndex).bind()
    val tableInstance = store.table(tableAddress).bind()

    val functionType = frame.state.module.definedType(typeIndex).bind()

    val elementIndex = stack.popI32().bind()
    val reference = tableInstance.element(elementIndex).bind()

    val address = when (reference) {
        is ReferenceValue.Function -> Ok(reference.address)
        else -> Err(InvocationError.IndirectCallOnANonFunctionReference)
    }.bind()

    val functionInstance = store.function(address).bind()

    if (functionInstance.type != functionType) {
        Err(InvocationError.IndirectCallHasIncorrectFunctionType).bind<Unit>()
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(store, stack, functionInstance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(store, stack, functionInstance, tailRecursion).bind()
    }
}
