package io.github.charlietap.chasm.executor.invoker.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.type.ext.functionType

inline fun FunctionInstance.functionType(): Result<FunctionType, InvocationError> =
    type.functionType()?.let(::Ok) ?: Err(InvocationError.FunctionCompositeTypeExpected)
