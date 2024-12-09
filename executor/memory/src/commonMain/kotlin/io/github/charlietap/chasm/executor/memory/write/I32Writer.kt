package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I32Writer = (LinearMemory, Int, Int) -> Result<Unit, InvocationError>

expect inline fun I32Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Result<Unit, InvocationError>
