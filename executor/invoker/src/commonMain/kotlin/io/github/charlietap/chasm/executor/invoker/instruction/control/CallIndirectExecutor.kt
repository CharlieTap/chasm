package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.WasmFunctionCall
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.ext.tableAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun CallIndirectExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallIndirect,
): Result<Unit, InvocationError> =
    CallIndirectExecutor(
        context = context,
        tableIndex = instruction.tableIndex,
        typeIndex = instruction.typeIndex,
        tailRecursion = false,
        hostFunctionCall = ::HostFunctionCall,
        wasmFunctionCall = ::WasmFunctionCall,
        definedTypeMatcher = ::DefinedTypeMatcher,
    )

internal inline fun CallIndirectExecutor(
    context: ExecutionContext,
    tableIndex: Index.TableIndex,
    typeIndex: Index.TypeIndex,
    tailRecursion: Boolean,
    crossinline hostFunctionCall: HostFunctionCall,
    crossinline wasmFunctionCall: WasmFunctionCall,
    crossinline definedTypeMatcher: TypeMatcher<DefinedType>,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()

    val tableAddress = frame.instance
        .tableAddress(tableIndex)
        .bind()
    val tableInstance = store.table(tableAddress).bind()

    val functionType = frame.instance
        .definedType(typeIndex)
        .bind()

    val elementIndex = stack.popI32().bind()
    val reference = tableInstance.element(elementIndex).bind()

    val address = when (reference) {
        is ReferenceValue.Function -> Ok(reference.address)
        else -> Err(InvocationError.IndirectCallOnANonFunctionReference)
    }.bind()

    val functionInstance = store.function(address).bind()

    if (!definedTypeMatcher(functionInstance.type, functionType, context)) {
        Err(InvocationError.IndirectCallHasIncorrectFunctionType).bind<Unit>()
    }

    when (functionInstance) {
        is FunctionInstance.HostFunction -> hostFunctionCall(context, functionInstance).bind()
        is FunctionInstance.WasmFunction -> wasmFunctionCall(context, functionInstance).bind()
    }
}
