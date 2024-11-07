package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun CompositeTypeValidator(
    context: ValidationContext,
    type: CompositeType,
): Result<Unit, ModuleValidatorError> =
    CompositeTypeValidator(
        context = context,
        type = type,
        arrayTypeValidator = ::ArrayTypeValidator,
        functionTypeValidator = ::FunctionTypeValidator,
        structTypeValidator = ::StructTypeValidator,
    )

internal inline fun CompositeTypeValidator(
    context: ValidationContext,
    type: CompositeType,
    crossinline arrayTypeValidator: Validator<ArrayType>,
    crossinline functionTypeValidator: Validator<FunctionType>,
    crossinline structTypeValidator: Validator<StructType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (type) {
        is CompositeType.Array -> arrayTypeValidator(context, type.arrayType).bind()
        is CompositeType.Function -> functionTypeValidator(context, type.functionType).bind()
        is CompositeType.Struct -> structTypeValidator(context, type.structType).bind()
    }
}
