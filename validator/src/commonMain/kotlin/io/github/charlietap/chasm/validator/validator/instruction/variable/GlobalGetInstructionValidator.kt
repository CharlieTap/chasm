package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.globalType
import io.github.charlietap.chasm.validator.ext.push

internal fun GlobalGetInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction.GlobalGet,
): Result<Unit, ModuleValidatorError> = binding {
    val globalType = context.globalType(instruction.globalIdx).bind()
    context.push(globalType.valueType)
}
