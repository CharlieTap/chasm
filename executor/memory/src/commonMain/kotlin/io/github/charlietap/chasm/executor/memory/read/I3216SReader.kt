package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I3216SReader = (LinearMemory, Int) -> Result<Int, InvocationError>

expect inline fun I3216SReader(
    memory: LinearMemory,
    address: Int,
): Result<Int, InvocationError>
