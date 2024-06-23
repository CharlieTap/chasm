package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun CallInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Call,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.functionIndex.idx.toInt() !in context.functions.indices) {
        Err(InstructionValidatorError.UnknownFunction).bind<Unit>()
    }

    val functionType = context.functionType(instruction.functionIndex).bind()
    context.labels.add(functionType.results)
}
