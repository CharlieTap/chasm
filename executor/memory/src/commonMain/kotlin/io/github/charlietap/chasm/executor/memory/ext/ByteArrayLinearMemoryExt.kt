@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal inline fun ByteArrayLinearMemory.grow(
    additionalPages: LinearMemory.Pages,
): Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits> {

    val proposedNumberOfPages = min.amount + additionalPages.amount

    if (proposedNumberOfPages > max.amount) {
        return Err(InvocationError.MemoryGrowExceedsLimits(additionalPages.amount, max.amount))
    }

    val newMemory = memory.copyOf(proposedNumberOfPages * LinearMemory.PAGE_SIZE)

    return Ok(ByteArrayLinearMemory(LinearMemory.Pages(proposedNumberOfPages), max, newMemory))
}
