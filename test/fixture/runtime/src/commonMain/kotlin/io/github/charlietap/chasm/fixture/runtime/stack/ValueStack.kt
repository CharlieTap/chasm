package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun vstack(
    values: List<ExecutionValue> = emptyList(),
) = ValueStack().apply {
    values.forEach { value ->
        push(value.toLongFromBoxed())
    }
}
