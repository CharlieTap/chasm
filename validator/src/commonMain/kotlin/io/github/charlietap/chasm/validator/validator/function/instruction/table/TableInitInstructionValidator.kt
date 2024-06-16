package io.github.charlietap.chasm.validator.validator.function.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TableValidatorError

internal fun TableInitInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableInit,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.tableIdx.idx.toInt() !in context.validTableIndices) {
        Err(TableValidatorError.UnknownTable).bind<Unit>()
    }
    if (instruction.elemIdx.idx.toInt() !in context.module.elementSegments.indices) {
        Err(TableValidatorError.UnknownSegmentIndex).bind<Unit>()
    }
}
