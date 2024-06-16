package io.github.charlietap.chasm.validator.validator.function.instruction.table

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun TableInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction,
): Result<Unit, ModuleValidatorError> =
    TableInstructionValidator(
        context = context,
        instruction = instruction,
        elementDropValidator = ::ElementDropInstructionValidator,
        tableInitValidator = ::TableInitInstructionValidator,
    )

internal fun TableInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction,
    elementDropValidator: Validator<TableInstruction.ElemDrop>,
    tableInitValidator: Validator<TableInstruction.TableInit>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is TableInstruction.ElemDrop -> elementDropValidator(context, instruction)
        is TableInstruction.TableCopy -> Ok(Unit)
        is TableInstruction.TableFill -> Ok(Unit)
        is TableInstruction.TableGet -> Ok(Unit)
        is TableInstruction.TableGrow -> Ok(Unit)
        is TableInstruction.TableInit -> tableInitValidator(context, instruction)
        is TableInstruction.TableSet -> Ok(Unit)
        is TableInstruction.TableSize -> Ok(Unit)
    }
}
