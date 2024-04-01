@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.type.ext

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.functionType
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpanderImpl

inline fun DefinedType.functionType(
    definedTypeExpander: DefinedTypeExpander = ::DefinedTypeExpanderImpl,
): Result<FunctionType, InvocationError> = definedTypeExpander(this).functionType()
