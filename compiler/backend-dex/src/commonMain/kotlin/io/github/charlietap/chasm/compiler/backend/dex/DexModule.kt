package io.github.charlietap.chasm.compiler.backend.dex

class DexModule(
    val dex: List<ByteArray>,
    val installer: String,
    val inlineMemory: Boolean,
    val chunkStarts: Map<Int, IntArray> = emptyMap(),
)

class UnsupportedFunctions(
    val reasons: Map<Int, String>,
) : IllegalArgumentException(
        "${reasons.size} functions cannot compile: " + reasons.entries.take(10).joinToString { (index, reason) -> "f$index: $reason" },
    )

class DexTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)
