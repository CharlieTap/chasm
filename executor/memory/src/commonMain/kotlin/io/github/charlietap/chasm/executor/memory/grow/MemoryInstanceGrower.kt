package io.github.charlietap.chasm.executor.memory.grow

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceGrower = (MemoryInstance, Int) -> Result<MemoryInstance, InvocationError.MemoryGrowExceedsLimits>
