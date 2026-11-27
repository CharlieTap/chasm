package io.github.charlietap.chasm.stack

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
sealed interface StackError {
    data object StackUnderflow : StackError

    data object StackOverflow : StackError
}
