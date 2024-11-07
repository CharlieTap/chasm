package io.github.charlietap.chasm.validator.validator.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.referenceType
import io.github.charlietap.chasm.validator.ext.tableType

internal fun TableInitInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableInit,
): Result<Unit, ModuleValidatorError> =
    TableInitInstructionValidator(
        context = context,
        instruction = instruction,
        typeMatcher = ::ReferenceTypeMatcher,
    )

internal fun TableInitInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableInit,
    typeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {

    val tableType = context.tableType(instruction.tableIdx).bind()
    val elementSegmentReferenceType = context.referenceType(instruction.elemIdx).bind()

    if (!typeMatcher(elementSegmentReferenceType, tableType.referenceType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    repeat(3) {
        context.popI32().bind()
    }
}
