package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.differ.ReferenceTypeDiffer
import io.github.charlietap.chasm.type.differ.TypeDiffer
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.ValueTypeMatcher
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.validator.type.ReferenceTypeValidator

internal fun BreakOnCastFailInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrOnCastFail,
): Result<Unit, ModuleValidatorError> =
    BreakOnCastFailInstructionValidator(
        context = context,
        instruction = instruction,
        referenceTypeDiffer = ::ReferenceTypeDiffer,
        referenceTypeValidator = ::ReferenceTypeValidator,
        referenceTypeMatcher = ::ReferenceTypeMatcher,
        valueTypeMatcher = ::ValueTypeMatcher,
    )

internal inline fun BreakOnCastFailInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrOnCastFail,
    crossinline referenceTypeDiffer: TypeDiffer<ReferenceType>,
    crossinline referenceTypeValidator: Validator<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline valueTypeMatcher: TypeMatcher<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {

    referenceTypeValidator(context, instruction.srcReferenceType).bind()
    referenceTypeValidator(context, instruction.dstReferenceType).bind()

    if (!referenceTypeMatcher(instruction.dstReferenceType, instruction.srcReferenceType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind()
    }

    val label = context.labels.peek(instruction.labelIndex).bind()

    val outputs = if (label.instruction is ControlInstruction.Loop) {
        label.inputs
    } else {
        label.outputs
    }

    if (outputs.types.isEmpty()) {
        Err(TypeValidatorError.TypeMismatch).bind()
    }

    val t0 = outputs.types.dropLast(1)
    val t1 = outputs.types.last()

    val diffed = referenceTypeDiffer(instruction.srcReferenceType, instruction.dstReferenceType)

    if (!valueTypeMatcher(ValueType.Reference(diffed), t1, context)) {
        Err(TypeValidatorError.TypeMismatch).bind()
    }

    context.popValues(t0 + listOf(ValueType.Reference(instruction.srcReferenceType)))
    context.pushValues(t0 + listOf(ValueType.Reference(instruction.dstReferenceType)))
}
