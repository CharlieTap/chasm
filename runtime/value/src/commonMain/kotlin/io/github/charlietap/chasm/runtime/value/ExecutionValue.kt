package io.github.charlietap.chasm.runtime.value

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
sealed interface ExecutionValue {
    data object Uninitialised : ExecutionValue
}
