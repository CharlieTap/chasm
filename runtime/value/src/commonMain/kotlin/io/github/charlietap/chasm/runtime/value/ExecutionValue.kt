package io.github.charlietap.chasm.runtime.value

sealed interface ExecutionValue {
    data object Uninitialised : ExecutionValue
}
