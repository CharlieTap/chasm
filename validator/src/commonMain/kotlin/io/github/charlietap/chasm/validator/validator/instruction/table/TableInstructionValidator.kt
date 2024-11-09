package io.github.charlietap.chasm.validator.validator.instruction.table

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
        tableCopyValidator = ::TableCopyInstructionValidator,
        tableFillValidator = ::TableFillInstructionValidator,
        tableGetValidator = ::TableGetInstructionValidator,
        tableSetValidator = ::TableSetInstructionValidator,
        tableGrowValidator = ::TableGrowInstructionValidator,
        tableInitValidator = ::TableInitInstructionValidator,
        tableSizeValidator = ::TableSizeInstructionValidator,
    )

internal inline fun TableInstructionValidator(
    context: ValidationContext,
    instruction: TableInstruction,
    crossinline elementDropValidator: Validator<TableInstruction.ElemDrop>,
    crossinline tableCopyValidator: Validator<TableInstruction.TableCopy>,
    crossinline tableFillValidator: Validator<TableInstruction.TableFill>,
    crossinline tableGetValidator: Validator<TableInstruction.TableGet>,
    crossinline tableSetValidator: Validator<TableInstruction.TableSet>,
    crossinline tableGrowValidator: Validator<TableInstruction.TableGrow>,
    crossinline tableInitValidator: Validator<TableInstruction.TableInit>,
    crossinline tableSizeValidator: Validator<TableInstruction.TableSize>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is TableInstruction.ElemDrop -> elementDropValidator(context, instruction)
        is TableInstruction.TableCopy -> tableCopyValidator(context, instruction)
        is TableInstruction.TableFill -> tableFillValidator(context, instruction)
        is TableInstruction.TableGet -> tableGetValidator(context, instruction)
        is TableInstruction.TableGrow -> tableGrowValidator(context, instruction)
        is TableInstruction.TableInit -> tableInitValidator(context, instruction)
        is TableInstruction.TableSet -> tableSetValidator(context, instruction)
        is TableInstruction.TableSize -> tableSizeValidator(context, instruction)
    }
}
