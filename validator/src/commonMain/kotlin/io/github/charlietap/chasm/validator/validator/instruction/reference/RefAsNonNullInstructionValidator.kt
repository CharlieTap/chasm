package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popReference
import io.github.charlietap.chasm.validator.ext.pushRef

internal fun RefAsNonNullInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefAsNonNull,
): Result<Unit, ModuleValidatorError> = binding {
    val ht = context.popReference().bind().heapType
    context.pushRef(ht)
}
