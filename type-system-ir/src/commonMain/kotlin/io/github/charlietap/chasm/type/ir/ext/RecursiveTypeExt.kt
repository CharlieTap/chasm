package io.github.charlietap.chasm.type.ir.ext

import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.FunctionType
import io.github.charlietap.chasm.ir.type.RecursiveType
import io.github.charlietap.chasm.ir.type.StructType
import io.github.charlietap.chasm.type.ir.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeRoller

inline fun RecursiveType.definedTypeIr(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
): DefinedType = definedTypeRoller(0, this).first()

inline fun RecursiveType.functionType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedTypeIr(definedTypeRoller).functionType(definedTypeExpander)

inline fun RecursiveType.structType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedTypeIr(definedTypeRoller).structType(definedTypeExpander)

inline fun RecursiveType.arrayType(
    definedTypeRoller: DefinedTypeRoller = ::DefinedTypeRoller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedTypeIr(definedTypeRoller).arrayType(definedTypeExpander)
