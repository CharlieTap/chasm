package io.github.charlietap.chasm.executor.memory.write

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceFloatWriter = (MemoryInstance, Float, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>
