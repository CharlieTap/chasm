package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias F32Writer = (LinearMemory, Int, Float) -> Result<Unit, InvocationError>

expect inline fun F32Writer(
    memory: LinearMemory,
    address: Int,
    value: Float,
): Result<Unit, InvocationError>
