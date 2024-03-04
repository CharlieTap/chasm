package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

internal typealias MemoryGrowthAllocator = (MemoryInstance, UInt) -> Result<MemoryInstance, InvocationError.MemoryGrowExceedsLimits>
