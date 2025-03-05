package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

typealias ValueMatcher = (ExecutionValue, ExecutionValue) -> Boolean

fun ValueMatcher(
    first: ExecutionValue,
    second: ExecutionValue,
): Boolean = when {
    first is NumberValue.F32 && second is NumberValue.F32 -> compareFloats(first.value, second.value)
    first is NumberValue.F64 && second is NumberValue.F64 -> compareDoubles(first.value, second.value)
    first is ReferenceValue.Null -> {
        when (first.heapType) {
            is AbstractHeapType.Any ->
                second == ReferenceValue.Null(AbstractHeapType.Any) ||
                    second == ReferenceValue.Null(AbstractHeapType.None)
            is AbstractHeapType.Array ->
                second == ReferenceValue.Null(AbstractHeapType.Array) ||
                    second is ReferenceValue.Array
            is AbstractHeapType.Bottom ->
                second is ReferenceValue
            is AbstractHeapType.Eq ->
                second == ReferenceValue.Null(AbstractHeapType.Eq) ||
                    second == ReferenceValue.Null(AbstractHeapType.Eq) ||
                    second is ReferenceValue.Array ||
                    second is ReferenceValue.I31 ||
                    second is ReferenceValue.Struct
            is AbstractHeapType.Extern ->
                second == ReferenceValue.Null(AbstractHeapType.Extern) ||
                    second == ReferenceValue.Null(AbstractHeapType.NoExtern) ||
                    second is ReferenceValue.Extern
            is AbstractHeapType.Func ->
                second == ReferenceValue.Null(AbstractHeapType.Func) ||
                    second == ReferenceValue.Null(AbstractHeapType.NoFunc) ||
                    second is ReferenceValue.Function
            is AbstractHeapType.I31 ->
                second == ReferenceValue.Null(AbstractHeapType.I31) ||
                    second is ReferenceValue.I31
            is AbstractHeapType.NoFunc ->
                second == ReferenceValue.Null(AbstractHeapType.Func) ||
                    second == ReferenceValue.Null(AbstractHeapType.NoFunc)
            is AbstractHeapType.NoExtern ->
                second == ReferenceValue.Null(AbstractHeapType.Extern) ||
                    second == ReferenceValue.Null(AbstractHeapType.NoExtern)
            is AbstractHeapType.None ->
                second == ReferenceValue.Null(AbstractHeapType.Any) ||
                    second == ReferenceValue.Null(AbstractHeapType.None)
            is AbstractHeapType.Struct ->
                second == ReferenceValue.Null(AbstractHeapType.Struct) ||
                    second is ReferenceValue.Struct
            else -> first == second
        }
    }
    else -> first == second
}

private fun compareFloats(first: Float, second: Float): Boolean = if (first.isNaN()) {
    second.isNaN()
} else {
    first == second
}

private fun compareDoubles(first: Double, second: Double): Boolean = if (first.isNaN()) {
    second.isNaN()
} else {
    first == second
}
