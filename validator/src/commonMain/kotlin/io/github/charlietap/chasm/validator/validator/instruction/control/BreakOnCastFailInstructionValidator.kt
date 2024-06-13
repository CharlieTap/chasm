package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popAndReplaceValues
import io.github.charlietap.chasm.validator.ext.push

internal fun BreakOnCastFailInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrOnCastFail,
): Result<Unit, ModuleValidatorError> = binding {

    val label = context.labels.peek(instruction.labelIndex).bind()

    val outputs = if (label.instruction is ControlInstruction.Loop) {
        label.inputs
    } else {
        label.outputs
    }

    context.pop(ValueType.Reference(instruction.srcReferenceType)).bind()

    val t0 = if (outputs.types.size == 1) {
        emptyList()
    } else {
        outputs.types.dropLast(1)
    }

    context.popAndReplaceValues(t0)
    context.push(ValueType.Reference(instruction.dstReferenceType))
}
