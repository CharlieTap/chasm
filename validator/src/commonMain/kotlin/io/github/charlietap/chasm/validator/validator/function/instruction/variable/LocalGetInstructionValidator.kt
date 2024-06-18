package io.github.charlietap.chasm.validator.validator.function.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TableValidatorError

internal fun LocalGetInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction.LocalGet,
): Result<Unit, ModuleValidatorError> = binding {
    if (instruction.localIdx.idx.toInt() !in context.locals.indices) {
        Err(TableValidatorError.UnknownSegmentIndex).bind<Unit>()
    }
}
