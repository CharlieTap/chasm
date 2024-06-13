package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun BreakTableInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.BrTable,
): Result<Unit, ModuleValidatorError> = binding {

    val defaultLabel = context.labels.peek(instruction.defaultLabelIndex).bind()

    val defaultOutputs = if (defaultLabel.instruction is ControlInstruction.Loop) {
        defaultLabel.inputs
    } else {
        defaultLabel.outputs
    }

    val arity = defaultOutputs.types.size

    context.popI32().bind()
    instruction.labelIndices.forEach { labelIndex ->

        val label = context.labels.peek(labelIndex).bind()

        val outputs = if (label.instruction is ControlInstruction.Loop) {
            label.inputs
        } else {
            label.outputs
        }

        if (outputs.types.size != arity) {
            Err(TypeValidatorError.TypeMismatch).bind<Unit>()
        }

        val results = context.popValues(outputs.types).bind()
        context.pushValues(results.asReversed())
    }

    context.popValues(defaultOutputs.types).bind()
    context.unreachable().bind()
}
