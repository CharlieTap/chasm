package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

typealias ValueMatcher = (ExecutionValue, ExecutionValue) -> Boolean

fun ValueMatcher(
    first: ExecutionValue,
    second: ExecutionValue,
): Boolean = when {
    first is NumberValue.F32 && second is NumberValue.F32 -> compareFloats(first.value, second.value)
    first is NumberValue.F64 && second is NumberValue.F64 -> compareDoubles(first.value, second.value)
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
