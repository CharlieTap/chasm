package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popTableAddress
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.tableType

internal fun CallIndirectValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallIndirect,
): Result<Unit, ModuleValidatorError> =
    CallIndirectValidator(
        context = context,
        instruction = instruction,
        typeMatcher = ::ReferenceTypeMatcher,
    )

internal inline fun CallIndirectValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallIndirect,
    crossinline typeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {

    val tableType = context.tableType(instruction.tableIndex).bind()
    if (!typeMatcher(tableType.referenceType, ReferenceType.RefNull(AbstractHeapType.Func), context)) {
        Err(InstructionValidatorError.CallIndirectOnNonFunction).bind()
    }

    val functionType = context.functionType(instruction.typeIndex).bind()

    context.popTableAddress(instruction.tableIndex).bind()
    context.popValues(functionType.params.types).bind()
    context.pushValues(functionType.results.types)
}
