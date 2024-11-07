package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ReferenceTypeValidator(
    context: ValidationContext,
    type: ReferenceType,
): Result<Unit, ModuleValidatorError> =
    ReferenceTypeValidator(
        context = context,
        type = type,
        heapTypeValidator = ::HeapTypeValidator,
    )

internal inline fun ReferenceTypeValidator(
    context: ValidationContext,
    type: ReferenceType,
    crossinline heapTypeValidator: Validator<HeapType>,
): Result<Unit, ModuleValidatorError> = binding {
    heapTypeValidator(context, type.heapType).bind()
}
