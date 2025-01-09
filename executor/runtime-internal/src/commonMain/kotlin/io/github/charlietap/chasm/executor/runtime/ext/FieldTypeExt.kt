package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun FieldType.default(): Result<ExecutionValue, InvocationError> = when (val storageType = this.storageType) {
    is StorageType.Packed -> Ok(storageType.type.default())
    is StorageType.Value -> storageType.type.default()
}
