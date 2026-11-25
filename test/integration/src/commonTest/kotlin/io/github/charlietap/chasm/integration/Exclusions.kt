package io.github.charlietap.chasm.integration

const val INTERPRETER = "interpreter"
const val COMPILED = "compiled"

val EXCLUSIONS: Map<String, Set<String>> = mapOf(
    COMPILED to setOf("simd", "threads"),
)

fun excludingManagedHeaps(script: String): Map<String, Set<String>> {
    return EXCLUSIONS + (COMPILED to EXCLUSIONS.getValue(COMPILED) + script)
}
