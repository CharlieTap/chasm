package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.runtime.execution.CompiledStack

private const val STACK_BYTES = 32L shl 20

fun resourceBytes(
    path: String,
): ByteArray = checkNotNull(CompiledStack::class.java.classLoader?.getResourceAsStream(path)) { "missing $path" }.use { it.readBytes() }

fun <T> onLargeStack(
    body: () -> T,
): T {
    var result: Result<T>? = null
    val budget = CompiledStack.budgetSource
    val thread = Thread(null, { result = runCatching(body) }, "wasm", STACK_BYTES)
    CompiledStack.budgetSource = { CompiledStack.budgetFor(STACK_BYTES) }
    try {
        thread.start()
        thread.join()
    } finally {
        CompiledStack.budgetSource = budget
    }
    return checkNotNull(result).getOrThrow()
}
