package io.github.charlietap.chasm.executor.runtime.exception

import io.github.charlietap.chasm.executor.runtime.error.InvocationError

class InvocationException(val error: InvocationError) : Exception()
