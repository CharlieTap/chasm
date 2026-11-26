package io.github.charlietap.chasm.compiler.backend.jvm

import java.io.OutputStream

interface JvmOutput {
    fun classFile(
        internalName: String,
        bytes: ByteArray,
    )

    fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T
}

class JvmModule(
    val moduleClass: String,
    val data: String?,
    /** How the code reads memory 0 directly, which the memory must then support, or null when it goes through helpers. */
    val inlineMemory: MemoryLowering? = null,
    /** Each chunked function's chunk start offsets, when lowered with stack traces. */
    val chunkStarts: Map<Int, IntArray> = emptyMap(),
)

const val DATA_EXTENSION = "chasmdata"

class ClassTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)

class UnsupportedFunctions(
    val reasons: Map<Int, String>,
) : IllegalArgumentException("cannot generate functions: " + reasons.entries.joinToString { (index, reason) -> "$index ($reason)" })
