package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I64Writer = (LinearMemory, Int, Long) -> Result<Unit, InvocationError>

expect inline fun I64Writer(
    memory: LinearMemory,
    address: Int,
    value: Long,
): Result<Unit, InvocationError>
