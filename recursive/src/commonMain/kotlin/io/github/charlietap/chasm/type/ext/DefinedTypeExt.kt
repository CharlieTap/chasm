@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.type.ext

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

inline fun DefinedType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpanderImpl,
): FunctionType? = definedTypeExpander(this).functionType()
