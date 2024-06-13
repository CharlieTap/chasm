package io.github.charlietap.chasm.validator.validator.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TableValidatorError

internal fun ElementDropInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.ElemDrop,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.elemIdx.idx.toInt() !in context.module.elementSegments.indices) {
        Err(TableValidatorError.UnknownSegmentIndex).bind<Unit>()
    }
}
