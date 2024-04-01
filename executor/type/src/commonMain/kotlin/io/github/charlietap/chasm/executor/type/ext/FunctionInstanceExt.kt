@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.type.ext

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance

inline fun FunctionInstance.functionType(): Result<FunctionType, InvocationError> =
    type.functionType()
