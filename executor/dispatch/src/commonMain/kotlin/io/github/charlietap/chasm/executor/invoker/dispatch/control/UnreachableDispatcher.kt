package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun UnreachableDispatcher(): DispatchableInstruction = DispatchableInstruction { _, _, _ ->
    throw InvocationException(InvocationError.Unreachable)
}
