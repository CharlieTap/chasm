package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal typealias LinearMemoryInteractor<T> = (LinearMemory, Int, Int, () -> T) -> Result<T, InvocationError.MemoryOperationOutOfBounds>
