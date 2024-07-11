package io.github.charlietap.chasm.stack

sealed interface StackError {
    data object StackUnderflow : StackError

    data object StackOverflow : StackError
}
