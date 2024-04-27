package io.github.charlietap.chasm.script.ext

fun mismatchTemplate(expected: Any, actual: Any): String = """

Arguments returned are a mismatch

Expected: $expected
Actual: $actual
    """.trimIndent()
