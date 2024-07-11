package io.github.charlietap.chasm.validator.validator.start

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.StartFunctionValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun StartFunctionValidator(
    context: ValidationContext,
    startFunction: StartFunction,
): Result<Unit, ModuleValidatorError> = binding {

    val type = context.functionType(startFunction.idx).bind()

    if (type.params.types.isNotEmpty() || type.results.types.isNotEmpty()) {
        Err(StartFunctionValidatorError.InvalidStartFunction).bind<Unit>()
    }
}
