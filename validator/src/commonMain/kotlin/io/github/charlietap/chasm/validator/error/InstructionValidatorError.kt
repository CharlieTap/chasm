package io.github.charlietap.chasm.validator.error

sealed interface InstructionValidatorError : ModuleValidatorError {
    data object UnknownInstruction : InstructionValidatorError

    data object UnknownDataSegment : InstructionValidatorError

    data object UnknownElementSegment : InstructionValidatorError

    data object UnknownFunction : InstructionValidatorError

    data object UnknownGlobal : InstructionValidatorError

    data object UnknownLabel : InstructionValidatorError

    data object UnknownLocal : InstructionValidatorError

    data object UnknownMemory : InstructionValidatorError

    data object UnknownReference : InstructionValidatorError

    data object UnknownTable : InstructionValidatorError

    data object UnknownTag : InstructionValidatorError

    data object UnnaturalMemoryAlignment : InstructionValidatorError

    data object MutationOfAConstGlobal : InstructionValidatorError

    data object ConstInstructionExpected : InstructionValidatorError
}
