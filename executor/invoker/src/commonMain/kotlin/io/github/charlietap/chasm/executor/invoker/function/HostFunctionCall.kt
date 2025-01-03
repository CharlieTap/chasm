package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue
import io.github.charlietap.chasm.host.HostFunctionException

internal typealias HostFunctionCall = (ExecutionContext, FunctionInstance.HostFunction) -> Result<Unit, InvocationError>

internal fun HostFunctionCall(
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val type = function.functionType

    val params = List(type.params.types.size) {
        stack.popValue().bind()
    }.asReversed()

    val functionContext = HostFunctionContext(
        store,
        frame.instance,
    )
    val results = try {
        function.function.invoke(functionContext, params)
    } catch (e: HostFunctionException) {
        Err(InvocationError.HostFunctionError(e.reason)).bind()
    }

    type.results.types.forEachIndexed { index, valueType ->
        val result = results.getOrNull(index)

        val typeMatches = classifyValue(valueType, result)

        if (typeMatches) {
            stack.push(result!!)
        } else {
            Err(InvocationError.HostFunctionInconsistentWithType(valueType, result)).bind<Unit>()
        }
    }
}

private fun classifyValue(valueType: ValueType, value: ExecutionValue?): Boolean {
    return when (valueType) {
        is ValueType.Number -> {
            when (valueType.numberType) {
                NumberType.I32 -> value is NumberValue.I32
                NumberType.I64 -> value is NumberValue.I64
                NumberType.F32 -> value is NumberValue.F32
                NumberType.F64 -> value is NumberValue.F64
            }
        }
        is ValueType.Reference -> {
            when (val referenceType = valueType.referenceType) {
                is ReferenceType.RefNull ->
                    when (referenceType.heapType) {
                        is AbstractHeapType.Func ->
                            value is ReferenceValue.Function ||
                                value is ReferenceValue.Null &&
                                value.heapType == AbstractHeapType.Func
                        is AbstractHeapType.Extern ->
                            value is ReferenceValue.Extern ||
                                value is ReferenceValue.Null &&
                                value.heapType == AbstractHeapType.Extern
                        is AbstractHeapType.Struct ->
                            value is ReferenceValue.Struct ||
                                value is ReferenceValue.Null &&
                                value.heapType == AbstractHeapType.Struct
                        is AbstractHeapType.Array ->
                            value is ReferenceValue.Array ||
                                value is ReferenceValue.Null &&
                                value.heapType == AbstractHeapType.Array
                        is AbstractHeapType.I31 ->
                            value is ReferenceValue.I31 ||
                                value is ReferenceValue.Null &&
                                value.heapType == AbstractHeapType.I31
                        else -> false
                    }
                is ReferenceType.Ref -> when (referenceType.heapType) {
                    is AbstractHeapType.Func -> value is ReferenceValue.Function
                    is AbstractHeapType.Extern -> value is ReferenceValue.Extern
                    is AbstractHeapType.Struct -> value is ReferenceValue.Struct
                    is AbstractHeapType.Array -> value is ReferenceValue.Array
                    is AbstractHeapType.I31 -> value is ReferenceValue.I31
                    else -> false
                }
            }
        }
        is ValueType.Vector -> {
            when (valueType.vectorType) {
                VectorType.V128 -> value is VectorValue.V128
            }
        }
        is ValueType.Bottom -> true
    }
}
