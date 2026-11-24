@file:JvmName("CompiledExceptions")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.jvm.JvmName

fun wasmException(
    failure: Throwable,
): Throwable {
    if (failure is InvocationException && failure.error == InvocationError.ThrownException) return failure
    if (failure is HostRaisedWasmException) return failure
    throw failure
}
