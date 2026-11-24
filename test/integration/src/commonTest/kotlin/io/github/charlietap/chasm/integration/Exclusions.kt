package io.github.charlietap.chasm.integration

const val INTERPRETER = "interpreter"
const val JIT = "jit"
const val AOT = "aot"

val EXCLUSIONS: Map<String, Set<String>> = mapOf(
    AOT to setOf("simd", "threads"),
)

fun excludingManagedHeaps(script: String): Map<String, Set<String>> {
    return EXCLUSIONS + (JIT to setOf(script)) + (AOT to EXCLUSIONS.getValue(AOT) + script)
}
