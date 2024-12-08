package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I328SReader = (LinearMemory, Int) -> Result<Int, InvocationError>

expect inline fun I328SReader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError>
