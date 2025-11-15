package com.tap.chasm.binary

import java.io.FileNotFoundException

internal object JvmBinaryLoader : BinaryLoader {
    override fun load(path: String): ByteArray {
        return this::class.java.classLoader.getResourceAsStream(path)?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource at path $path")
    }
}

actual fun binaryLoaderFactory(): BinaryLoader {
    return JvmBinaryLoader
}
