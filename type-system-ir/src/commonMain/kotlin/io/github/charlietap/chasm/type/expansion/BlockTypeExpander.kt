package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.ResultType
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
            definedTypes[type.typeIndex.idx].functionType()
        }
        is ControlInstruction.BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}
