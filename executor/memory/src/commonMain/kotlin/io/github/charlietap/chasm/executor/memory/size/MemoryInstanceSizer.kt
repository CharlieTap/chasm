package io.github.charlietap.chasm.executor.memory.size

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceSizer = (MemoryInstance) -> Result<Int, InvocationError>
