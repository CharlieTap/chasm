package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues

internal fun CallInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Call,
): Result<Unit, ModuleValidatorError> = binding {
    val functionType = context.functionType(instruction.functionIndex).bind()

    context.popValues(functionType.params.types).bind()
    context.pushValues(functionType.results.types)
}
