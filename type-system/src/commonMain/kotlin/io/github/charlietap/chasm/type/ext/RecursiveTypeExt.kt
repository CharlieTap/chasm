package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller

inline fun RecursiveType.definedType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
): DefinedType = definedTypeRoller(Int.MIN_VALUE, this).first()

inline fun RecursiveType.functionType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedType(definedTypeRoller).functionType(definedTypeExpander)

inline fun RecursiveType.structType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedType(definedTypeRoller).structType(definedTypeExpander)

inline fun RecursiveType.arrayType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedType(definedTypeRoller).arrayType(definedTypeExpander)
