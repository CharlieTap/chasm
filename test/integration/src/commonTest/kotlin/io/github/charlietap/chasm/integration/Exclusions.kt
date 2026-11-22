package io.github.charlietap.chasm.integration

const val INTERPRETER = "interpreter"
const val AOT = "aot"

val EXCLUSIONS: Map<String, Set<String>> = mapOf(
    AOT to setOf("simd", "threads"),
)
