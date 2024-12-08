package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias F64Reader = (LinearMemory, Int) -> Result<Double, InvocationError>

expect inline fun F64Reader(
    memory: LinearMemory,
    address: Int,
): Result<Double, InvocationError>
