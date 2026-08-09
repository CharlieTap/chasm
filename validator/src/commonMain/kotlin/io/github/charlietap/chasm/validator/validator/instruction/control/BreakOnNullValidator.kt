package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.branchValues
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.popAndReplaceValues
import io.github.charlietap.chasm.validator.ext.popReference
import io.github.charlietap.chasm.validator.ext.push

internal fun BreakOnNullInstructionValidator(
    context: ModuleValidationContext,
    instruction: ControlInstruction.BrOnNull,
): Result<Unit, ModuleValidatorError> = binding {

    val label = context.labels.peek(instruction.labelIndex).bind()

    val outputs = label.branchValues

    val referenceType = context.popReference().bind()
    context.popAndReplaceValues(outputs.types).bind()
    context.push(ValueType.Reference(ReferenceType.Ref(referenceType.heapType)))
}
