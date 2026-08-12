package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.validator.context.CoreTypeValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun RecursiveTypeValidator(
    context: CoreTypeValidationContext,
    type: RecursiveType,
): Result<Unit, ModuleValidatorError> =
    RecursiveTypeValidator(
        context = context,
        type = type,
        subTypeValidator = ::SubTypeValidator,
    )

internal inline fun RecursiveTypeValidator(
    context: CoreTypeValidationContext,
    type: RecursiveType,
    crossinline subTypeValidator: (CoreTypeValidationContext, SubType, Int) -> Result<Unit, ModuleValidatorError>,
): Result<Unit, ModuleValidatorError> = binding {
    val firstTypeIndex = context.definedTypeCount - type.subTypes.size
    type.subTypes.forEachIndexed { index, subType ->
        subTypeValidator(context, subType, firstTypeIndex + index).bind()
    }
}
