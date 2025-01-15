package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.type.copy.DeepCopier
import io.github.charlietap.chasm.type.copy.RecursiveTypeDeepCopier
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun RecursiveTypeValidator(
    context: ValidationContext,
    type: RecursiveType,
): Result<Unit, ModuleValidatorError> =
    RecursiveTypeValidator(
        context = context,
        type = type,
        definedTypeRoller = ::DefinedTypeRoller,
        subTypeValidator = ::SubTypeValidator,
        recursiveTypeCopier = ::RecursiveTypeDeepCopier,
    )

internal inline fun RecursiveTypeValidator(
    context: ValidationContext,
    type: RecursiveType,
    crossinline definedTypeRoller: DefinedTypeRoller,
    crossinline subTypeValidator: Validator<SubType>,
    crossinline recursiveTypeCopier: DeepCopier<RecursiveType>,
): Result<Unit, ModuleValidatorError> = binding {

    // the act of rolling mutates the recursive type with the
    // recursive type indices but this validator expects the type
    // to be in syntax state

    val copy = recursiveTypeCopier(type)
    val definedType = definedTypeRoller(context.types.size, copy)
    context.types += definedType

    type.subTypes.forEach { subType ->
        subTypeValidator(context, subType).bind()
    }
}
