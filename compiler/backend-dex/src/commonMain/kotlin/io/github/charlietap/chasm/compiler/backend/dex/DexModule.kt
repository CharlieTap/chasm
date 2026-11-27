package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.InternalChasmApi
import java.io.OutputStream

@InternalChasmApi
interface DexResources {

    fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T
}

@InternalChasmApi
class DexModule(
    val dex: List<ByteArray>,
    val moduleClass: String,
    val classes: List<String>,
)

@InternalChasmApi
class UnsupportedModule(
    message: String,
) : IllegalArgumentException(message)

@InternalChasmApi
class DexTooLarge(
    message: String,
    cause: Throwable,
) : IllegalArgumentException(message, cause)
