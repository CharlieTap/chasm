package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.matching.HeapTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.popTableAddress
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.tableType
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ReturnCallIndirectInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ReturnCallIndirect,
): Result<Unit, ModuleValidatorError> =
    ReturnCallIndirectInstructionValidator(
        context = context,
        instruction = instruction,
        typeMatcher = ::HeapTypeMatcher,
    )

internal inline fun ReturnCallIndirectInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.ReturnCallIndirect,
    crossinline typeMatcher: TypeMatcher<HeapType>,
): Result<Unit, ModuleValidatorError> = binding {

    val tableType = context.tableType(instruction.tableIndex).bind()
    if (!typeMatcher(AbstractHeapType.Func, tableType.referenceType.heapType, context)) {
        Err(InstructionValidatorError.CallIndirectOnNonFunction).bind()
    }

    val functionType = context.functionType(instruction.typeIndex).bind()
    if (functionType.results != context.result) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    context.popTableAddress(instruction.tableIndex).bind()

    context.popValues(functionType.params.types).bind()
    context.pushValues(functionType.results.types)

    context.popValues(context.result?.types ?: emptyList()).bind()
    context.unreachable().bind()
}
