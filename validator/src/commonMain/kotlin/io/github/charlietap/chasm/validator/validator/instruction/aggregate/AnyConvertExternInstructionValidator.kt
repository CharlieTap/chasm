package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.push

internal fun AnyConvertExternInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.AnyConvertExtern,
): Result<Unit, ModuleValidatorError> = binding {
    val actual = context.pop(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern))).bind()

    val extern = when ((actual as ValueType.Reference).referenceType) {
        is ReferenceType.Ref -> ReferenceType.Ref(AbstractHeapType.Any)
        is ReferenceType.RefNull -> ReferenceType.RefNull(AbstractHeapType.Any)
    }

    context.push(ValueType.Reference(extern))
}
