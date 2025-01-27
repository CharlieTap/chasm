package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

fun StructInstance.field(
    index: Index.FieldIndex,
): FieldValue.Execution = try {
    this.fields[index.idx.toInt()] as FieldValue.Execution
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
}

fun StructInstance.packedField(
    index: Index.FieldIndex,
): FieldValue.Packed = try {
    this.fields[index.idx.toInt()] as FieldValue.Packed
} catch (_: IndexOutOfBoundsException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.StructFieldLookupFailed(index.idx.toInt()))
}
