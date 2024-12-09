package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I64Reader = (LinearMemory, Int) -> Result<Long, InvocationError>

expect inline fun I64Reader(
    memory: LinearMemory,
    address: Int,
): Result<Long, InvocationError>
