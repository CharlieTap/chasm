package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.referenceType
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpack

internal fun ArrayInitElemInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayInitElement,
): Result<Unit, ModuleValidatorError> =
    ArrayInitElemInstructionValidator(
        context = context,
        instruction = instruction,
        referenceTypeMatcher = ::ReferenceTypeMatcher,
    )

internal inline fun ArrayInitElemInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayInitElement,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val arrayType = definedType
        .arrayType(context.unroller)
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    if (arrayType.fieldType.mutability != Mutability.Var) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    val t = arrayType.fieldType.unpack()

    val arrayReferenceType = when (t) {
        is ValueType.Reference -> Ok(t.referenceType)
        else -> Err(TypeValidatorError.TypeMismatch)
    }.bind()

    val elementSegmentReferenceType = context.referenceType(instruction.elementIndex).bind()

    if (!referenceTypeMatcher(elementSegmentReferenceType, arrayReferenceType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    repeat(3) {
        context.popI32().bind()
    }
    context.pop(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(definedType)))).bind()
}
