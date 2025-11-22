package io.github.charlietap.chasm.validator.validator.instruction.atomic

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun AtomicFenceInstructionValidator(
    @Suppress("UNUSED_PARAMETER")
    context: ValidationContext,
    @Suppress("UNUSED_PARAMETER")
    instruction: AtomicMemoryInstruction.Fence,
): Result<Unit, ModuleValidatorError> = Ok(Unit)
