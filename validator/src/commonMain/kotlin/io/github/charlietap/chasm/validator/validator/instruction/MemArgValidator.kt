@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun MemArgValidator(
    context: ValidationContext,
    arg: MemArg,
): Result<Unit, ModuleValidatorError> = binding {
    when (arg.align) {
        in 0u..2u -> Unit
        else -> Err(InstructionValidatorError.UnnaturalMemoryAlignment).bind<Unit>()
    }
}
