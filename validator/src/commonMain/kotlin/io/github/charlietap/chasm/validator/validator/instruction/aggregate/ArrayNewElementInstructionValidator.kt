package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.referenceType
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpack

internal fun ArrayNewElementInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayNewElement,
): Result<Unit, ModuleValidatorError> =
    ArrayNewElementInstructionValidator(
        context = context,
        instruction = instruction,
        ::ReferenceTypeMatcher,
    )

internal inline fun ArrayNewElementInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayNewElement,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val arrayType = definedType.arrayType().toResultOr {
        TypeValidatorError.TypeMismatch
    }.bind()

    val t = arrayType.fieldType.unpack()

    val arrayReferenceType = when (t) {
        is ValueType.Reference -> Ok(t.referenceType)
        else -> Err(TypeValidatorError.TypeMismatch)
    }.bind()

    val elementSegmentReferenceType = context.referenceType(instruction.elementIndex).bind()

    if (!referenceTypeMatcher(elementSegmentReferenceType, arrayReferenceType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    repeat(2) {
        context.popI32().bind()
    }
    context.push(ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(definedType))))
}
