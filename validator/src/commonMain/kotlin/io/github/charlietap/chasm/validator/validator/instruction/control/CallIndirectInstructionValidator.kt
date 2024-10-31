package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.matching.HeapTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.tableType
import io.github.charlietap.chasm.validator.ext.type

internal fun CallIndirectValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallIndirect,
): Result<Unit, ModuleValidatorError> =
    CallIndirectValidator(
        context = context,
        instruction = instruction,
        typeMatcher = ::HeapTypeMatcher,
    )

internal inline fun CallIndirectValidator(
    context: ValidationContext,
    instruction: ControlInstruction.CallIndirect,
    crossinline typeMatcher: TypeMatcher<HeapType>,
): Result<Unit, ModuleValidatorError> = binding {

    val tableType = context.tableType(instruction.tableIndex).bind()
    if (!typeMatcher(AbstractHeapType.Func, tableType.referenceType.heapType, context)) {
        Err(InstructionValidatorError.CallIndirectOnNonFunction).bind()
    }

    val definedType = context.type(instruction.typeIndex).bind()
    val functionType = definedType.functionType().toResultOr {
        InstructionValidatorError.UnknownFunction
    }.bind()

    context.popI32().bind()
    context.popValues(functionType.params.types).bind()
    context.pushValues(functionType.results.types)
}
