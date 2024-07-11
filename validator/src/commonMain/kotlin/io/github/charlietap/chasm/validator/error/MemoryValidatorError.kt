package io.github.charlietap.chasm.validator.error

sealed interface MemoryValidatorError : ModuleValidatorError {
    data object MultipleMemories : MemoryValidatorError
}
