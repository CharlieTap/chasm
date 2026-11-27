package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
fun ValueType.default(): Long = defaultBits() ?: throw InvocationException(InvocationError.UndefinedDefaultBottomType)
