package io.github.charlietap.chasm.executor.memory.read

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceBytesReader = (MemoryInstance, Int, Int) -> Result<ByteArray, InvocationError.MemoryOperationOutOfBounds>
