package io.github.charlietap.chasm.runtime.execution

fun interface NestedInterpreter {
    fun interpret(
        entryIp: Int,
        context: ExecutionContext,
    )
}
