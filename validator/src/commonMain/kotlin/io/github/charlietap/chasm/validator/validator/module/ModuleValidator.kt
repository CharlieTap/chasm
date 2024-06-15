package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.ValidationContext
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.function.FunctionValidator
import io.github.charlietap.chasm.validator.validator.import.ImportValidator

internal fun ModuleValidator(
    context: ValidationContext,
    module: Module,
): Result<Unit, ModuleValidatorError> =
    ModuleValidator(
        context = context,
        module = module,
        functionValidator = ::FunctionValidator,
        importValidator = ::ImportValidator,
        multipleMemoriesValidator = ::MultipleMemoriesValidator,
    )

internal fun ModuleValidator(
    context: ValidationContext,
    module: Module,
    functionValidator: Validator<Function>,
    importValidator: Validator<Import>,
    multipleMemoriesValidator: Validator<Module>,
): Result<Unit, ModuleValidatorError> = binding {

    multipleMemoriesValidator(context, module).bind()

    module.apply {
        functions.forEach { function ->
            functionValidator(context, function).bind()
        }
        imports.forEach { import ->
            importValidator(context, import).bind()
        }
    }
}
