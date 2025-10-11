package com.tap.chasm.binary

interface BinaryLoader {
    fun load(path: String): ByteArray
}

expect fun binaryLoaderFactory(): BinaryLoader
