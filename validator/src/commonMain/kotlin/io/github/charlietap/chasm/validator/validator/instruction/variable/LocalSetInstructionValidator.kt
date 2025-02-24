package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.localType
import io.github.charlietap.chasm.validator.ext.pop

internal fun LocalSetInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction.LocalSet,
): Result<Unit, ModuleValidatorError> = binding {

    val localType = context.localType(instruction.localIdx).bind()
    context.locals[instruction.localIdx.idx.toInt()] = localType.copy(
        status = InitializationStatus.SET,
    )

    context.pop(localType.type).bind()
}
