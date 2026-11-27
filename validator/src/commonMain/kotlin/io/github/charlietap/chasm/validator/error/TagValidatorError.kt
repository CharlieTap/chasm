package io.github.charlietap.chasm.validator.error

internal sealed interface TagValidatorError : ModuleValidatorError {
    data object InvalidTagType : TagValidatorError
}
