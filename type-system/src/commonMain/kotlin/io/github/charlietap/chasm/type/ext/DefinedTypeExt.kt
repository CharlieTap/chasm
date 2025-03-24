package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

inline fun DefinedType.functionType(
    noinline definedTypeUnroller: DefinedTypeUnroller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): FunctionType? = definedTypeExpander(this, definedTypeUnroller).functionType()

inline fun DefinedType.structType(
    noinline definedTypeUnroller: DefinedTypeUnroller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): StructType? = definedTypeExpander(this, definedTypeUnroller).structType()

inline fun DefinedType.arrayType(
    noinline definedTypeUnroller: DefinedTypeUnroller,
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpander,
): ArrayType? = definedTypeExpander(this, definedTypeUnroller).arrayType()
