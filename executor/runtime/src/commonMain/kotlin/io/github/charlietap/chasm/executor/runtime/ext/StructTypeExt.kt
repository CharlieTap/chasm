package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

fun StructType.field(
    index: Index.FieldIndex,
): Result<FieldType, InvocationError> = fields
    .getOrNull(index.idx.toInt())
    .toResultOr { InvocationError.StructFieldLookupFailed(index.idx.toInt()) }
