package io.github.charlietap.chasm.jit.internal

internal data class CompileError(
    val error: String,
    val stage: Stage,
    val functions: List<Int> = emptyList(),
) {

    enum class Stage {
        GENERATION,
        LOADING,
    }

    fun message(): String = buildString {
        append("JIT ")
        append(stage.name.lowercase())
        append(" failed: ")
        append(error)
        if (functions.isNotEmpty()) functions.joinTo(this, prefix = " (functions ", postfix = ")")
    }
}
