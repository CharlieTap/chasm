package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction

fun functionBatches(
    functions: List<CirFunction>,
    maxInstructions: Int,
    maxFunctions: Int,
): List<List<CirFunction>> {
    val batches = ArrayList<List<CirFunction>>()
    var current = ArrayList<CirFunction>()
    var instructions = 0
    for (function in functions) {
        val full = instructions + function.instructions.size > maxInstructions || current.size >= maxFunctions
        if (current.isNotEmpty() && full) {
            batches += current
            current = ArrayList()
            instructions = 0
        }
        current += function
        instructions += function.instructions.size
    }
    if (current.isNotEmpty()) batches += current
    return batches
}
