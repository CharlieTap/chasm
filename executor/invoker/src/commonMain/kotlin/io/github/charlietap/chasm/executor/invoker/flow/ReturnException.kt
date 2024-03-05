package io.github.charlietap.chasm.executor.invoker.flow

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class ReturnException(
    val results: List<ExecutionValue>,
) : Exception()
