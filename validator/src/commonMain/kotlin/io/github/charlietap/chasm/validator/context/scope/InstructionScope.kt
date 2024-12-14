package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.validator.context.InstructionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun InstructionScope(
    context: ValidationContext,
    instruction: Instruction,
): Result<ValidationContext, ModuleValidatorError> = context
    .copy(
        instructionContext = InstructionContextImpl(
            instruction = instruction,
        ),
    ).let(::Ok)
