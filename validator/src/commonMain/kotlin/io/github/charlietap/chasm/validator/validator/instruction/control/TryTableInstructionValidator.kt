package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.ValueTypeMatcher
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.functionType
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.pop
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues
import io.github.charlietap.chasm.validator.ext.tagType
import io.github.charlietap.chasm.validator.validator.instruction.InstructionBlockValidator
import kotlin.math.max

internal fun TryTableInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.TryTable,
): Result<Unit, ModuleValidatorError> = TryTableInstructionValidator(
    context = context,
    instruction = instruction,
    instructionBlockValidator = ::InstructionBlockValidator,
    typeMatcher = ::ValueTypeMatcher,
)

internal inline fun TryTableInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.TryTable,
    crossinline instructionBlockValidator: Validator<List<Instruction>>,
    crossinline typeMatcher: TypeMatcher<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {

    val functionType = context.functionType(instruction.blockType).bind()
    context.popValues(functionType.params.types)

    instruction.handlers.forEach { handler ->

        val label = context.labels.peek(handler.labelIndex).bind()

        when (handler) {
            is ControlInstruction.CatchHandler.Catch -> {
                val tagType = context.tagType(handler.tagIndex).bind()

                if (tagType.type.results.types
                        .isNotEmpty()
                ) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }

                val tagTypeArity = tagType.type.params.types.size
                val labelResultArity = label.outputs.types.size

                for (idx in 0 until max(tagTypeArity, labelResultArity)) {
                    val tagValue = tagType.type.params.types
                        .getOrNull(idx)
                    val labelValue = label.outputs.types.getOrNull(idx)

                    if (tagValue == null || labelValue == null || !typeMatcher(tagValue, labelValue, context)) {
                        Err(TypeValidatorError.TypeMismatch).bind()
                    }
                }
            }
            is ControlInstruction.CatchHandler.CatchRef -> {
                val tagType = context.tagType(handler.tagIndex).bind()

                if (tagType.type.results.types
                        .isNotEmpty()
                ) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }

                val exnRef = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Exception))
                val tagValues = tagType.type.params.types + exnRef
                val labelValues = label.outputs.types

                val tagTypeArity = tagValues.size
                val labelResultArity = labelValues.size

                for (idx in 0 until max(tagTypeArity, labelResultArity)) {
                    val tagValue = tagValues.getOrNull(idx)
                    val labelValue = labelValues.getOrNull(idx)

                    if (tagValue == null || labelValue == null || !typeMatcher(tagValue, labelValue, context)) {
                        Err(TypeValidatorError.TypeMismatch).bind()
                    }
                }
            }
            is ControlInstruction.CatchHandler.CatchAllRef -> {

                val exnRef = ValueType.Reference(ReferenceType.Ref(AbstractHeapType.Exception))
                if (label.outputs.types.isEmpty() || !typeMatcher(exnRef, label.outputs.types.first(), context)) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }
            }
            is ControlInstruction.CatchHandler.CatchAll -> {
                if (label.outputs.types.isNotEmpty()) {
                    Err(TypeValidatorError.TypeMismatch).bind()
                }
            }
        }
    }

    val exceptionHandler = Label(
        instruction = instruction,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        unreachable = false,
    )
    context.labels.push(exceptionHandler)
    context.pushValues(functionType.params.types)

    instructionBlockValidator(context, instruction.instructions).bind()
    context.labels.pop().bind()
}
