@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

inline fun CompositeType.functionType(): Result<FunctionType, InvocationError> {
    return (this as? CompositeType.Function)?.functionType?.let(::Ok) ?: Err(InvocationError.FunctionCompositeTypeExpected)
}

inline fun CompositeType.structType(): Result<StructType, InvocationError> {
    return (this as? CompositeType.Struct)?.structType?.let(::Ok) ?: Err(InvocationError.StructCompositeTypeExpected)
}

inline fun CompositeType.arrayType(): Result<ArrayType, InvocationError> {
    return (this as? CompositeType.Array)?.arrayType?.let(::Ok) ?: Err(InvocationError.ArrayCompositeTypeExpected)
}
