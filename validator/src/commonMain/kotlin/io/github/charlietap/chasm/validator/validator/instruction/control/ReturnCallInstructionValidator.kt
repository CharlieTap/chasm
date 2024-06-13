package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ReturnCallInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ReturnCall,
): Result<Unit, ModuleValidatorError> = binding {

    val functionType = context.functionType(instruction.functionIndex).bind()

    if (functionType.results != context.result) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    context.popValues(functionType.params.types).bind()
    context.pushValues(functionType.results.types)

    context.popValues(context.result?.types?.asReversed() ?: emptyList()).bind()
    context.unreachable().bind()
}
