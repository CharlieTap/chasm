package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias F64Writer = (LinearMemory, Int, Double) -> Result<Unit, InvocationError>

expect inline fun F64Writer(
    memory: LinearMemory,
    address: Int,
    value: Double,
): Result<Unit, InvocationError>
