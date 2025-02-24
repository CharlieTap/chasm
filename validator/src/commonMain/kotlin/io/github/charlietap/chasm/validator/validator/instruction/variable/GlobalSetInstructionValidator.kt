package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.globalType
import io.github.charlietap.chasm.validator.ext.pop

internal fun GlobalSetInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction.GlobalSet,
): Result<Unit, ModuleValidatorError> = binding {
    val globalType = context.globalType(instruction.globalIdx).bind()
    if (globalType.mutability == Mutability.Const) {
        Err(InstructionValidatorError.MutationOfAConstGlobal).bind<Unit>()
    }

    context.pop(globalType.valueType).bind()
}
