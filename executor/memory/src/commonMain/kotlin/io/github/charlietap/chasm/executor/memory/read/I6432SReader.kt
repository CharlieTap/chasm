package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I6432SReader = (LinearMemory, Int) -> Result<Long, InvocationError>

expect inline fun I6432SReader(
    memory: LinearMemory,
    address: Int,
): Result<Long, InvocationError>
