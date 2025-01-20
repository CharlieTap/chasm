package io.github.charlietap.chasm.executor.instantiator.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

inline fun ModuleInstance.definedType(index: Index.TypeIndex): Result<DefinedType, InvocationError.FunctionTypeLookupFailed> =
    types.getOrNull(index.idx.toInt())?.let(::Ok) ?: Err(InvocationError.FunctionTypeLookupFailed(index.idx.toInt()))
