package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal fun HeapTypeValidator(
    context: ValidationContext,
    type: HeapType,
): Result<Unit, ModuleValidatorError> =
    HeapTypeValidator(
        context = context,
        type = type,
        definedTypeValidator = ::DefinedTypeValidator,
    )

internal inline fun HeapTypeValidator(
    context: ValidationContext,
    type: HeapType,
    crossinline definedTypeValidator: Validator<DefinedType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (type) {
        is AbstractHeapType -> Unit
        is ConcreteHeapType.Defined -> definedTypeValidator(context, type.definedType).bind()
        is ConcreteHeapType.RecursiveTypeIndex -> Unit
        is ConcreteHeapType.TypeIndex -> {
            if (type.index.idx.toInt() !in context.types.indices) {
                Err(TypeValidatorError.TypeMismatch).bind()
            }
        }
    }
}
