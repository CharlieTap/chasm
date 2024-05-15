package io.github.charlietap.chasm.executor.memory.init

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceInitialiser = (DataInstance, MemoryInstance, IntRange, IntRange) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>
