package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
fun interface NestedInterpreter {
    fun interpret(
        entryIp: Int,
        context: ExecutionContext,
    )
}
