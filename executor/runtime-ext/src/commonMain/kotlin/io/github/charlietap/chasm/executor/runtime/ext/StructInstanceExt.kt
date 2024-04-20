package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

fun StructInstance.field(
    index: Index.FieldIndex,
): Result<FieldValue, InvocationError> = this.fields
    .getOrNull(index.idx.toInt())
    .toResultOr { InvocationError.StructFieldLookupFailed(index.idx.toInt()) }
