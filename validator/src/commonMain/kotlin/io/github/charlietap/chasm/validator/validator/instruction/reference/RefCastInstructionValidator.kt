package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.type.extremas.TopOf
import io.github.charlietap.chasm.type.extremas.TopOfHeapType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.push

internal fun RefCastInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefCast,
): Result<Unit, ModuleValidatorError> =
    RefCastInstructionValidator(
        context = context,
        instruction = instruction,
        topOfHeapType = ::TopOfHeapType,
    )

internal inline fun RefCastInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline topOfHeapType: TopOf<HeapType>,
): Result<Unit, ModuleValidatorError> = binding {

    val superType = topOfHeapType(instruction.referenceType.heapType, context.types).toResultOr {
        TypeValidatorError.TypeMismatch
    }.bind()

    val expected = ValueType.Reference(ReferenceType.RefNull(superType))
    context.pop(expected).bind()

    context.push(ValueType.Reference(instruction.referenceType))
}
