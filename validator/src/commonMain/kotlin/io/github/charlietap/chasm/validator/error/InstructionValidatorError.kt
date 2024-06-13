package io.github.charlietap.chasm.validator.error

sealed interface InstructionValidatorError : ModuleValidatorError {
    data object UnknownInstruction : InstructionValidatorError

    data object UnnaturalMemoryAlignment : InstructionValidatorError
}
