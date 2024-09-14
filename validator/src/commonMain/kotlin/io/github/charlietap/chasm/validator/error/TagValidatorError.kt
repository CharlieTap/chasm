package io.github.charlietap.chasm.validator.error

sealed interface TagValidatorError : ModuleValidatorError {
    data object InvalidTagType : TagValidatorError
}
