package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryGrower = (LinearMemory, Int) -> Result<LinearMemory, InvocationError>

expect inline fun LinearMemoryGrower(
    memory: LinearMemory,
    pagesToAdd: Int,
): Result<LinearMemory, InvocationError>
