package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.expansion.BlockTypeExpander

inline fun BlockType.asFunctionType(
    types: List<DefinedType>,
    expander: BlockTypeExpander = ::BlockTypeExpander,
): FunctionType {
    return expander(types, this) as FunctionType
}
