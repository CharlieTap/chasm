package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun BreakTableInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrTable,
): Result<Unit, ModuleValidatorError> = binding {

    val labels = instruction.labelIndices + instruction.defaultLabelIndex

    labels.forEach { label ->
        if (label.idx.toInt() !in context.labels.indices) {
            Err(InstructionValidatorError.UnknownLabel).bind<Unit>()
        }
    }
}
