package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.runtime.error.InvocationError

class InvocationException(val error: InvocationError) : Exception()
