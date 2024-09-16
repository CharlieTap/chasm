@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.type.ext.functionType

typealias BlockTypeExpander = (List<DefinedType>, ControlInstruction.BlockType) -> FunctionType?

inline fun BlockTypeExpander(
    definedTypes: List<DefinedType>,
    type: ControlInstruction.BlockType,
): FunctionType? {
    return when (type) {
        is ControlInstruction.BlockType.Empty -> FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        is ControlInstruction.BlockType.SignedTypeIndex -> {
            definedTypes[type.typeIndex.idx.toInt()].functionType()
        }
        is ControlInstruction.BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}
