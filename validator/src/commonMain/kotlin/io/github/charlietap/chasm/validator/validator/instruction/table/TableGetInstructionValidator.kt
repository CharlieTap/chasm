package io.github.charlietap.chasm.validator.validator.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.tableType

internal fun TableGetInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction.TableGet,
): Result<Unit, ModuleValidatorError> = binding {

    val tableType = context.tableType(instruction.tableIdx).bind()

    context.popI32().bind()
    context.push(ValueType.Reference(tableType.referenceType))
}
