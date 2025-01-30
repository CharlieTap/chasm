package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.toFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun CallIndirectExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirect,
) = CallIndirectExecutor(
    context = context,
    table = instruction.table,
    type = instruction.type,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::WasmFunctionCall,
    definedTypeMatcher = ::DefinedTypeMatcher,
)

internal inline fun CallIndirectExecutor(
    context: ExecutionContext,
    table: TableInstance,
    type: DefinedType,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
    crossinline definedTypeMatcher: TypeMatcher<DefinedType>,
) {
    val stack = context.vstack
    val store = context.store

    val elementIndex = stack.popI32()
    val address = table.element(elementIndex).toFunctionAddress()

    val functionInstance = store.function(address)
    if (!definedTypeMatcher(functionInstance.type, type, context)) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, functionInstance)
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, functionInstance)
    }
}
