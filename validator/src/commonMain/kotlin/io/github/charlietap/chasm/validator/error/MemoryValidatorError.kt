package io.github.charlietap.chasm.validator.error

internal sealed interface MemoryValidatorError : ModuleValidatorError {
    data object MultipleMemories : MemoryValidatorError
}
