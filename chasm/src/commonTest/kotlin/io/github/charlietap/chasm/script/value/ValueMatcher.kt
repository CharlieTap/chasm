package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.Value

typealias ValueMatcher = (Value, Value) -> Boolean

fun ValueMatcher(
    first: Value,
    second: Value,
): Boolean = when(first) {
    is Value.Number.F32 if second is Value.Number.F32 -> compareFloats(first.value, second.value)
    is Value.Number.F64 if second is Value.Number.F64 -> compareDoubles(first.value, second.value)
    is Value.Reference.Null -> {
        when (first.heapType) {
            is HeapType.Any ->
                second == Value.Reference.Null(HeapType.Any) ||
                    second == Value.Reference.Null(HeapType.None)
            is HeapType.Array ->
                second == Value.Reference.Null(HeapType.Array) ||
                    second is Value.Reference.Array
            is HeapType.Bottom ->
                second is Value.Reference
            is HeapType.Eq ->
                second == Value.Reference.Null(HeapType.Eq) ||
                    second == Value.Reference.Null(HeapType.Eq) ||
                    second is Value.Reference.Array ||
                    second is Value.Reference.I31 ||
                    second is Value.Reference.Struct
            is HeapType.Extern ->
                second == Value.Reference.Null(HeapType.Extern) ||
                    second == Value.Reference.Null(HeapType.NoExtern) ||
                    second is Value.Reference.Extern
            is HeapType.Func ->
                second == Value.Reference.Null(HeapType.Func) ||
                    second == Value.Reference.Null(HeapType.NoFunc) ||
                    second is Value.Reference.Func
            is HeapType.I31 ->
                second == Value.Reference.Null(HeapType.I31) ||
                    second is Value.Reference.I31
            is HeapType.NoFunc ->
                second == Value.Reference.Null(HeapType.Func) ||
                    second == Value.Reference.Null(HeapType.NoFunc)
            is HeapType.NoExtern ->
                second == Value.Reference.Null(HeapType.Extern) ||
                    second == Value.Reference.Null(HeapType.NoExtern)
            is HeapType.None ->
                second == Value.Reference.Null(HeapType.Any) ||
                    second == Value.Reference.Null(HeapType.None)
            is HeapType.Struct ->
                second == Value.Reference.Null(HeapType.Struct) ||
                    second is Value.Reference.Struct
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
