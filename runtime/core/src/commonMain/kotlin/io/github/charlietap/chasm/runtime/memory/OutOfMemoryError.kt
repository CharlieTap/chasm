package io.github.charlietap.chasm.runtime.memory

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
class OutOfMemoryError(
    message: String? = null,
    cause: Throwable? = null,
) : Error(message, cause)
