package io.github.charlietap.chasm.dex

data class CompileError(
    val message: String,
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
