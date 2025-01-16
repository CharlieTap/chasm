package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.definedType
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

    val (stack, store) = context
    val frame = stack.peekFrame()

    val expectedFunctionType = frame.instance
        .definedType(typeIndex)
        .bind()

    val elementIndex = stack.popI32()
    val reference = table.element(elementIndex)

    val address = when (reference) {
        is ReferenceValue.Function -> Ok(reference.address)
        else -> Err(InvocationError.IndirectCallOnANonFunctionReference)
    }.bind()

    val functionInstance = store.function(address).bind()
    val actualFunctionType = functionInstance.type

    if (!definedTypeMatcher(actualFunctionType, expectedFunctionType, context)) {
        Err(InvocationError.IndirectCallHasIncorrectFunctionType).bind()
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, functionInstance)
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, functionInstance)
    }
}
