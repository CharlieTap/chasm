package io.github.charlietap.chasm.validator.error

sealed interface InstructionValidatorError : ModuleValidatorError {
    data object UnknownInstruction : InstructionValidatorError

    data object UnknownFunction : InstructionValidatorError

    data object UnknownGlobal : InstructionValidatorError

    data object UnknownLabel : InstructionValidatorError

    data object UnnaturalMemoryAlignment : InstructionValidatorError
}
