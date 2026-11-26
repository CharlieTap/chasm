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
)

class ClassTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)
