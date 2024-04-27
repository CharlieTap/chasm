package io.github.charlietap.chasm.executor.memory.fill

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceFiller = (MemoryInstance, IntRange, Byte) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>
