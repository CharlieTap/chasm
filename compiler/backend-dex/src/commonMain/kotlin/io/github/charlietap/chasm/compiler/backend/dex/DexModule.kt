package io.github.charlietap.chasm.compiler.backend.dex

import java.io.OutputStream

interface DexResources {

    fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T
}

class DexModule(
    val dex: List<ByteArray>,
    val moduleClass: String,
    val classes: List<String>,
)

class UnsupportedModule(
    message: String,
) : IllegalArgumentException(message)

class DexTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)
