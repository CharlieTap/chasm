package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.initializeLocal
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.push

internal fun LocalTeeInstructionValidator(
    context: ModuleValidationContext,
    instruction: VariableInstruction.LocalTee,
): Result<Unit, ModuleValidatorError> = binding {

    val localType = context.initializeLocal(instruction.localIdx).bind()

    context.pop(localType.type).bind()
    context.push(localType.type)
}
