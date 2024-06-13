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
import io.github.charlietap.chasm.validator.ext.tableType

internal fun TableCopyInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableCopy,
): Result<Unit, ModuleValidatorError> =
    TableCopyInstructionValidator(
        context = context,
        instruction = instruction,
        typeMatcher = ::ReferenceTypeMatcher,
    )

internal fun TableCopyInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableCopy,
    typeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {
    val srcTableType = context.tableType(instruction.srcTableIdx).bind()
    val dstTableType = context.tableType(instruction.destTableIdx).bind()

    if (!typeMatcher(srcTableType.referenceType, dstTableType.referenceType, context)) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    repeat(3) {
        context.popI32().bind()
    }
}
