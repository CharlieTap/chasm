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
)

const val DATA_EXTENSION = "chasmdata"

class ClassTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)

class UnsupportedFunctions(
    val reasons: Map<Int, String>,
) : IllegalArgumentException("cannot generate functions: " + reasons.entries.joinToString { (index, reason) -> "$index ($reason)" })
