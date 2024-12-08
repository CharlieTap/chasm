package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I32Reader = (LinearMemory, Int) -> Result<Int, InvocationError>

expect inline fun I32Reader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError>
