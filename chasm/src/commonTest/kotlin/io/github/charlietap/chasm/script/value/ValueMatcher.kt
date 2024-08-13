package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.embedding.shapes.Value

typealias ValueMatcher = (Value, Value) -> Boolean

fun ValueMatcher(
    first: Value,
    second: Value,
): Boolean = when {
    first is Value.Number.F32 && second is Value.Number.F32 -> compareFloats(first.value, second.value)
    first is Value.Number.F64 && second is Value.Number.F64 -> compareDoubles(first.value, second.value)
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
