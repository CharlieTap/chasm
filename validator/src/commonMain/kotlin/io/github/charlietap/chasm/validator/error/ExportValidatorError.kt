package io.github.charlietap.chasm.validator.error

sealed interface ExportValidatorError : ModuleValidatorError {
    data object UnknownFunction : ExportValidatorError

    data object UnknownGlobal : ExportValidatorError

    data object UnknownMemory : ExportValidatorError

    data object UnknownTable : ExportValidatorError

    data object UnknownTag : ExportValidatorError

    data object DuplicateExportNames : ExportValidatorError
}
