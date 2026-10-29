package io.github.charlietap.chasm.aot

data class CompileError(
    val error: String,
    val stage: Stage,
    val functions: List<Int> = emptyList(),
) {

    enum class Stage {
        CONFIGURATION,
        INPUT,
        DECODE,
        VALIDATION,
        GENERATION,
        OUTPUT,
    }
}
