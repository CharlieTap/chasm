package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType

fun StructType.field(
    index: Index.FieldIndex,
): FieldType {
    val runtimeIndex = index.toInt()
    return try {
        fields[runtimeIndex]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.StructFieldLookupFailed(runtimeIndex))
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.StructFieldLookupFailed(runtimeIndex))
    }
}
