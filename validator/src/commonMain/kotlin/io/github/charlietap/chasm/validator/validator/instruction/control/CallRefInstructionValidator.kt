package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.type

internal fun CallRefInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallRef,
): Result<Unit, ModuleValidatorError> = binding {

    val definedType = context.type(instruction.typeIndex).bind()
    val functionType = context.functionType(instruction.typeIndex).bind()

    context.popValues(
        functionType.params.types + listOf(ValueType.Reference(ReferenceType.RefNull(ConcreteHeapType.Defined(definedType)))),
    ).bind()
    context.pushValues(functionType.results.types)
}
