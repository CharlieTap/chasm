package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.ext.functionType

internal typealias BlockTypeExpander = (ModuleInstance, ControlInstruction.BlockType) -> Result<FunctionType?, InvocationError>

internal inline fun BlockTypeExpander(
    instance: ModuleInstance,
    type: ControlInstruction.BlockType,
): Result<FunctionType?, InvocationError> = binding {
    when (type) {
        is ControlInstruction.BlockType.Empty -> null
        is ControlInstruction.BlockType.SignedTypeIndex -> {
            instance.types[type.typeIndex.idx.toInt()].functionType().toResultOr {
                InvocationError.FunctionCompositeTypeExpected
            }.bind()
        }
        is ControlInstruction.BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}
