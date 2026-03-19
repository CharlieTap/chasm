package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ext.functionType

typealias LegacyBlockTypeExpander = (List<DefinedType>, BlockType) -> FunctionType?

inline fun LegacyBlockTypeExpander(
    definedTypes: List<DefinedType>,
    type: BlockType,
): FunctionType? {
    return when (type) {
        is BlockType.Empty -> FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        is BlockType.SignedTypeIndex -> {
            definedTypes[type.typeIndex].functionType()
        }
        is BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}

typealias BlockTypeExpander = (List<RTT>, BlockType) -> FunctionType?

inline fun BlockTypeExpander(
    definedTypes: List<RTT>,
    type: BlockType,
): FunctionType? {
    return when (type) {
        is BlockType.Empty -> FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        is BlockType.SignedTypeIndex -> {
            definedTypes[type.typeIndex].type.functionType()
        }
        is BlockType.ValType -> {
            FunctionType(ResultType(emptyList()), ResultType(listOf(type.valueType)))
        }
    }
}
