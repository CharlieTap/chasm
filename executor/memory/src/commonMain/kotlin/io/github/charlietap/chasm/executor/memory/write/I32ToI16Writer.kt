package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias I32ToI16Writer = (LinearMemory, Int, Int) -> Result<Unit, InvocationError>

expect inline fun I32ToI16Writer(
    memory: LinearMemory,
    address: Int,
    value: Int,
): Result<Unit, InvocationError>
