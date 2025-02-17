package io.github.charlietap.chasm.type.ir.ext

import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.StructType
import io.github.charlietap.chasm.type.ir.expansion.DefinedTypeExpander

inline fun DefinedType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedTypeExpander(this).functionType()

inline fun DefinedType.structType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedTypeExpander(this).structType()

inline fun DefinedType.arrayType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedTypeExpander(this).arrayType()
