package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

data class ExceptionInstance(
    val tagAddress: Address.Tag,
    val fields: List<ExecutionValue>,
)
