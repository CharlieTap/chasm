@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory.Companion.PAGE_SIZE

internal inline fun ByteArrayLinearMemory.grow(
    additionalPages: LinearMemory.Pages,
): Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits> {

    val newSize = memory.size + (additionalPages.amount * PAGE_SIZE)
    val newMemory = memory.copyOf(newSize)

    return Ok(ByteArrayLinearMemory(newMemory))
}
