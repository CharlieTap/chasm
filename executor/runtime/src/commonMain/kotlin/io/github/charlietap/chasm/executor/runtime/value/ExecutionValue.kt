package io.github.charlietap.chasm.executor.runtime.value

sealed interface ExecutionValue {
    data object Uninitialised : ExecutionValue
}
