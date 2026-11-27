package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.InternalChasmApi
import java.io.OutputStream

@InternalChasmApi
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

@InternalChasmApi
class JvmModule(
    val moduleClass: String,
)

@InternalChasmApi
class ClassTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)
