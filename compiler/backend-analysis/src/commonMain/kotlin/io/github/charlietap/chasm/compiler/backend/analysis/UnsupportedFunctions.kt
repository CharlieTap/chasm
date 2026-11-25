package io.github.charlietap.chasm.compiler.backend.analysis

class UnsupportedFunctions(
    val reasons: Map<Int, String>,
) : IllegalArgumentException("cannot generate functions: " + reasons.entries.joinToString { (index, reason) -> "$index ($reason)" })
