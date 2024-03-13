@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun TableInstance.grow(
    elementsToAdd: Int,
    initialisationValue: ReferenceValue,
): Result<TableInstance, InvocationError> {

    val proposedLength = elementsToAdd + elements.size
    val max = type.limits.max
    if (max != null && proposedLength.toUInt() > max) {
        return Err(InvocationError.TableGrowExceedsLimits(max))
    }

    repeat(elementsToAdd) {
        elements.add(initialisationValue)
    }

    val newLimits = type.limits.copy(min = proposedLength.toUInt())

    return Ok(copy(type.copy(limits = newLimits)))
}
