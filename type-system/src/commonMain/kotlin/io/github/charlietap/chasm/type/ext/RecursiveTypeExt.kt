package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

inline fun RecursiveType.definedType(): DefinedType = DefinedType(
    typeIndex = Int.MIN_VALUE,
    recursiveType = this,
    recursiveTypeIndex = 0,
)

inline fun RecursiveType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedType().functionType(definedTypeExpander)

inline fun RecursiveType.structType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedType().structType(definedTypeExpander)

inline fun RecursiveType.arrayType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedType().arrayType(definedTypeExpander)
