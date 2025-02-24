package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popAndReplaceValues

internal fun BreakOnNonNullInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrOnNonNull,
): Result<Unit, ModuleValidatorError> = binding {

    val label = context.labels.peek(instruction.labelIndex).bind()

    val outputs = if (label.instruction is ControlInstruction.Loop) {
        label.inputs
    } else {
        label.outputs
    }

    if (outputs.types.isEmpty()) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    val t1 = outputs.types.last()
    if (t1 !is ValueType.Reference) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }
    val ht = (t1 as ValueType.Reference).referenceType.heapType

    val t0 = if (outputs.types.size == 1) {
        emptyList()
    } else {
        outputs.types.dropLast(1)
    }

    context.pop(ValueType.Reference(ReferenceType.RefNull(ht))).bind()
    context.popAndReplaceValues(t0).bind()
}
