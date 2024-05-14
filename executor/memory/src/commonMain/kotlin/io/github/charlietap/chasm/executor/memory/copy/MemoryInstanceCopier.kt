package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceCopier = (MemoryInstance, IntRange, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>
