package io.github.charlietap.chasm.validator.validator.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.structType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.push
import io.github.charlietap.chasm.validator.ext.type
import io.github.charlietap.chasm.validator.ext.unpack

internal fun StructNewInstructionValidator(
    context: ValidationContext,
    instruction: AggregateInstruction.StructNew,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val structType = definedType
        .structType()
        .toResultOr {
            TypeValidatorError.TypeMismatch
        }.bind()

    structType.fields.asReversed().forEach { fieldType ->
        val expected = fieldType.unpack()
        context.pop(expected).bind()
    }

    context.push(ValueType.Reference(ReferenceType.Ref(ConcreteHeapType.Defined(definedType))))
}
