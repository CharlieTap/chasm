package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias F32Reader = (LinearMemory, Int) -> Result<Float, InvocationError>

expect inline fun F32Reader(
    memory: LinearMemory,
    address: Int,
): Result<Float, InvocationError>
