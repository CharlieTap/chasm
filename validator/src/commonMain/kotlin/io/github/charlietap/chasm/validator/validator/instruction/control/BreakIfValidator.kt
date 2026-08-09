package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.branchValues
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.popAndReplaceValues
import io.github.charlietap.chasm.validator.ext.popI32

internal fun BreakIfInstructionValidator(
    context: ModuleValidationContext,
    instruction: ControlInstruction.BrIf,
): Result<Unit, ModuleValidatorError> = binding {

    val label = context.labels.peek(instruction.labelIndex).bind()

    val outputs = label.branchValues

    context.popI32().bind()
    context.popAndReplaceValues(outputs.types).bind()
}
