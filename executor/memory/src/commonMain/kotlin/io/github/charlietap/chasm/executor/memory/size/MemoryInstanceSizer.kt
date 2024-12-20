package io.github.charlietap.chasm.executor.memory.size

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias MemoryInstanceSizer = (MemoryInstance) -> Result<Int, InvocationError>

fun MemoryInstanceSizerImpl(
    instance: MemoryInstance,
): Result<Int, InvocationError> = Ok(
    instance.type.limits.min
        .toInt() * LinearMemory.PAGE_SIZE,
)
