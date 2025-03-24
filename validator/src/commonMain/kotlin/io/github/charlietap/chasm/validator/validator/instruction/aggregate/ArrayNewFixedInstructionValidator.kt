package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpack

internal fun ArrayNewFixedInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.ArrayNewFixed,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val arrayType = definedType
        .arrayType(context.unroller)
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    val t = arrayType.fieldType.unpack()

    repeat(instruction.size.toInt()) {
        context.pop(t).bind()
    }
    context.push(ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(definedType))))
}
