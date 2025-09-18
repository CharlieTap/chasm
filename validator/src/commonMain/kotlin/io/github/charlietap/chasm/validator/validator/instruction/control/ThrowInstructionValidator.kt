package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TagValidatorError
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.tagType
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ThrowInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Throw,
): Result<Unit, ModuleValidatorError> = binding {

    val tagType = context.tagType(instruction.tagIndex).bind()
    val functionType = tagType.functionType

    if (functionType.results.types.isNotEmpty()) {
        Err(TagValidatorError.InvalidTagType).bind()
    }

    context.popValues(functionType.params.types).bind()
    context.unreachable().bind()
}
