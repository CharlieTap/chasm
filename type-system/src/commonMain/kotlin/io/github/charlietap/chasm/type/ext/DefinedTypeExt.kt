package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

inline fun DefinedType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedTypeExpander(this).functionType()

inline fun DefinedType.structType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedTypeExpander(this).structType()

inline fun DefinedType.arrayType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedTypeExpander(this).arrayType()
