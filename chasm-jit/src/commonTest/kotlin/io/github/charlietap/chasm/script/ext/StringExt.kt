package io.github.charlietap.chasm.script.ext

import io.github.charlietap.chasm.jit.resourceBytes

fun mismatchTemplate(expected: Any, actual: Any): String = """

Arguments returned are a mismatch

Expected: $expected
Actual: $actual
    """.trimIndent()

fun String.readBytesFromPath(): ByteArray = resourceBytes(SWEET_RESOURCES + replace('\\', '/').substringAfter(SWEET_INTERMEDIATES))

fun String.readTextFromPath(): String = readBytesFromPath().decodeToString()

private const val SWEET_RESOURCES = "sweet/"
private const val SWEET_INTERMEDIATES = "/sweet/intermediates/"
