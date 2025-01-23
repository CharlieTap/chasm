package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun CallIndirectExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirect,
) = CallIndirectExecutor(
    context = context,
    typeIndex = instruction.typeIndex,
    table = instruction.table,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::WasmFunctionCall,
    definedTypeMatcher = ::DefinedTypeMatcher,
)

internal inline fun CallIndirectExecutor(
    context: ExecutionContext,
    table: TableInstance,
    typeIndex: Index.TypeIndex,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
    crossinline definedTypeMatcher: TypeMatcher<DefinedType>,
) {

    val stack = context.vstack
    val store = context.store
    val frame = context.cstack.peekFrame()

    val expectedFunctionType = frame.instance.definedType(typeIndex)

    val elementIndex = stack.popI32()
    val reference = table.element(elementIndex)

    val address = when (reference) {
        is ReferenceValue.Function -> reference.address
        else -> throw InvocationException(InvocationError.IndirectCallOnANonFunctionReference)
    }

    val functionInstance = store.function(address)
    val actualFunctionType = functionInstance.type

    if (!definedTypeMatcher(actualFunctionType, expectedFunctionType, context)) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, functionInstance)
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, functionInstance)
    }
}
