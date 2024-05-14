package io.github.charlietap.chasm.script.ext

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.io.readString

fun mismatchTemplate(expected: Any, actual: Any): String = """

Arguments returned are a mismatch

Expected: $expected
Actual: $actual
    """.trimIndent()

@OptIn(ExperimentalStdlibApi::class)
fun String.readBytesFromPath(): ByteArray {
    val source = SystemFileSystem.source(Path(this)).buffered()
    return source.use {
        it.readByteArray()
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun String.readTextFromPath(): String {
    val source = SystemFileSystem.source(Path(this)).buffered()
    return source.use {
        it.readString()
    }
}
