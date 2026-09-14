package io.github.charlietap.chasm.runtime.memory

class OutOfMemoryError(
    message: String? = null,
    cause: Throwable? = null,
) : Error(message, cause)
