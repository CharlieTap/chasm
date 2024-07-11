@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

inline fun DefinedType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpanderImpl,
): FunctionType? = definedTypeExpander(this).functionType()

inline fun DefinedType.structType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpanderImpl,
): StructType? = definedTypeExpander(this).structType()

inline fun DefinedType.arrayType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpanderImpl,
): ArrayType? = definedTypeExpander(this).arrayType()
