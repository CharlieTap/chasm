package io.github.charlietap.chasm.integration

const val INTERPRETER = "interpreter"
const val AOT = "aot"
const val JIT = "jit"

val EXCLUSIONS: Map<String, Set<String>> = mapOf(
    JIT to setOf(
        "store_limits_deep_stack",
        "trap_diagnostics_deep_trace",
    ),
)
