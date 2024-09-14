package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ThrowRefInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ThrowRef,
): Result<Unit, ModuleValidatorError> = binding {
    val exnref = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Exception))
    context.pop(exnref).bind()
    context.unreachable().bind()
}
