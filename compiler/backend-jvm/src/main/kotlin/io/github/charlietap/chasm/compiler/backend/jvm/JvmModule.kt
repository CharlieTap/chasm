package io.github.charlietap.chasm.compiler.backend.jvm

interface JvmOutput {
    fun classFile(
        internalName: String,
        bytes: ByteArray,
    )
}

class JvmModule(
    val moduleClass: String,
)

class ClassTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)

class UnsupportedFunctions(
    val reasons: Map<Int, String>,
) : IllegalArgumentException("cannot generate functions: " + reasons.entries.joinToString { (index, reason) -> "$index ($reason)" })
