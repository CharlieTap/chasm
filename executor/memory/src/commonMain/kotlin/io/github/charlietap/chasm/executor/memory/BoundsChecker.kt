package io.github.charlietap.chasm.executor.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

typealias BoundsChecker<T> = (Int, Int, Int, () -> T) -> Result<T, InvocationError.MemoryOperationOutOfBounds>
