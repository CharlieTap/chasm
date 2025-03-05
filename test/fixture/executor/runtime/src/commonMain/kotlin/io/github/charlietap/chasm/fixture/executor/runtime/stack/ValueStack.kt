package io.github.charlietap.chasm.fixture.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun vstack(
    values: List<ExecutionValue> = emptyList(),
) = ValueStack().apply {
    values.forEach { value ->
        push(value.toLongFromBoxed())
    }
}
