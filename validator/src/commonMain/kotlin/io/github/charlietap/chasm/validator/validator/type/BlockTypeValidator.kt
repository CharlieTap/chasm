package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.FunctionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun BlockTypeValidator(
    context: ValidationContext,
    type: BlockType,
): Result<Unit, ModuleValidatorError> =
    BlockTypeValidator(
        context = context,
        type = type,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun BlockTypeValidator(
    context: ValidationContext,
    type: BlockType,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (type) {
        BlockType.Empty -> Unit
        is BlockType.SignedTypeIndex -> {
            if (type.typeIndex !in context.types.indices) {
                Err(FunctionValidatorError.UnknownType).bind()
            }
        }
        is BlockType.ValType -> valueTypeValidator(context, type.valueType).bind()
    }
}
