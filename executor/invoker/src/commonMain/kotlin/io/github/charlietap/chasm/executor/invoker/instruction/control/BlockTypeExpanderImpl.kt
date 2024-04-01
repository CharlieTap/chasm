@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.type.ext.functionType

internal inline fun BlockTypeExpanderImpl(
    instance: ModuleInstance,
    type: ControlInstruction.BlockType,
): Result<FunctionType?, InvocationError> = binding {
    when (type) {
        is ControlInstruction.BlockType.Empty -> null
        is ControlInstruction.BlockType.SignedTypeIndex -> {
            instance.types[type.typeIndex.idx.toInt()].functionType().bind()
        }
        is ControlInstruction.BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}
