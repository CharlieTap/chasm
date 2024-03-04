package io.github.charlietap.chasm.executor.instantiator.allocation.table

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias TableGrowthAllocator = (TableInstance, UInt, ReferenceValue) -> Result<TableInstance, InvocationError.TableGrowExceedsLimits>
