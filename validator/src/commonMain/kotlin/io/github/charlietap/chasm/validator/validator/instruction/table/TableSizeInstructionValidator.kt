package io.github.charlietap.chasm.validator.validator.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.tableType

internal fun TableSizeInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableSize,
): Result<Unit, ModuleValidatorError> = binding {

    context.tableType(instruction.tableIdx).bind()

    context.pushI32()
}
