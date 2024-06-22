package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun CallIndirectValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallIndirect,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.tableIndex.idx.toInt() !in context.tables.indices) {
        Err(InstructionValidatorError.UnknownTable).bind<Unit>()
    }

    val functionType = context.functionType(instruction.typeIndex).bind()
    context.labels.add(functionType.results)
}
