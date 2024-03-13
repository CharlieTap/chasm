package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.invoker.ext.popValueOrError
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

@Suppress("UNUSED_PARAMETER")
internal fun HostFunctionCallImpl(
    store: Store,
    stack: Stack,
    function: FunctionInstance.HostFunction,
): Result<Unit, InvocationError> = binding {
    val type = function.type

    val params = List(type.params.types.size) {
        stack.popValueOrError().bind().value
    }.asReversed()

    val results = function.function.invoke(params)

    type.results.types.forEachIndexed { index, valueType ->
        val result = results.getOrNull(index)

        val typeMatches = classifyValue(valueType, result)

        if (typeMatches) {
            stack.push(Stack.Entry.Value(result!!))
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
            when (valueType.referenceType) {
                ReferenceType.Funcref ->
                    value is ReferenceValue.FunctionAddress ||
                        value is ReferenceValue.Null && value.referenceType == valueType.referenceType
                ReferenceType.Externref ->
                    value is ReferenceValue.FunctionAddress ||
                        value is ReferenceValue.Null && value.referenceType == valueType.referenceType
            }
        }
        is ValueType.Vector -> {
            when (valueType.vectorType) {
                VectorType.V128 -> value is VectorValue.V128
            }
        }
    }
}
