package io.github.charlietap.chasm.validator.validator.function.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun LoopInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Loop,
): Result<Unit, ModuleValidatorError> = binding {

    val resultType = when (val blockType = instruction.blockType) {
        ControlInstruction.BlockType.Empty -> ResultType(emptyList())
        is ControlInstruction.BlockType.SignedTypeIndex -> {
            val type = context.module.types[blockType.typeIndex.idx.toInt()]
            type.recursiveType.definedType().functionType()!!.results
        }
        is ControlInstruction.BlockType.ValType -> ResultType(listOf(blockType.valueType))
    }

    context.labels.add(resultType)
}
