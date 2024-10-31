package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.type.matching.CompositeTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.type

internal fun SubTypeValidator(
    context: ValidationContext,
    type: SubType,
): Result<Unit, ModuleValidatorError> =
    SubTypeValidator(
        context = context,
        type = type,
        compositeTypeMatcher = ::CompositeTypeMatcher,
        compositeTypeValidator = ::CompositeTypeValidator,
        definedTypeUnroller = ::DefinedTypeUnroller,
        heapTypeValidator = ::HeapTypeValidator,
    )

internal inline fun SubTypeValidator(
    context: ValidationContext,
    type: SubType,
    crossinline compositeTypeMatcher: TypeMatcher<CompositeType>,
    crossinline compositeTypeValidator: Validator<CompositeType>,
    crossinline definedTypeUnroller: DefinedTypeUnroller,
    crossinline heapTypeValidator: Validator<HeapType>,
): Result<Unit, ModuleValidatorError> = binding {
    compositeTypeValidator(context, type.compositeType).bind()
    type.superTypes.forEach { superType ->
        heapTypeValidator(context, superType).bind()
        when (superType) {
            is ConcreteHeapType.TypeIndex -> {
                val definedType = context.type(superType.index).bind()
                val subType = definedTypeUnroller(definedType)
                if (subType is SubType.Final) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }
                if (!compositeTypeMatcher(type.compositeType, subType.compositeType, context)) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }
            }
            else -> Err(TypeValidatorError.TypeMismatch).bind()
        }
    }
}
