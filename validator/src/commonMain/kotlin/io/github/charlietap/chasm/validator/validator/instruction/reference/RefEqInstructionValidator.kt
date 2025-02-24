package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.pushI32

internal fun RefEqInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefEq,
): Result<Unit, ModuleValidatorError> = binding {

    val eqRef = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq))

    context.pop(eqRef).bind()
    context.pop(eqRef).bind()

    context.pushI32()
}
