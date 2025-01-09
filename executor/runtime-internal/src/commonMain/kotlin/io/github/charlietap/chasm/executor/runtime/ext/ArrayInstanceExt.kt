package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

fun ArrayInstance.field(
    index: Index.FieldIndex,
): Result<FieldValue, InvocationError> = this.fields
    .getOrNull(index.idx.toInt())
    .toResultOr { InvocationError.ArrayFieldLookupFailed(index.idx.toInt()) }
