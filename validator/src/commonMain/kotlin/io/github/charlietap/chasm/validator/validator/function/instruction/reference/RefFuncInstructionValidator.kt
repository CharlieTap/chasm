package io.github.charlietap.chasm.validator.validator.function.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun RefFuncInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefFunc,
): Result<Unit, ModuleValidatorError> = binding {
    context.functionType(instruction.funcIdx).bind()
    if (instruction.funcIdx !in context.refs) {
        Err(InstructionValidatorError.UnknownReference).bind<Unit>()
    }
}
