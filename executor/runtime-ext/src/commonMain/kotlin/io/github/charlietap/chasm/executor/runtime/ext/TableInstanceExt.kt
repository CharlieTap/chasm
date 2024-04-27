@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

inline fun TableInstance.grow(
    elementsToAdd: Int,
    initialisationValue: ReferenceValue,
): Result<TableInstance, InvocationError> {

    val proposedLength = (elementsToAdd + elements.size).toUInt()

    val range = type.limits.min..(type.limits.max ?: UInt.MAX_VALUE)
    if (proposedLength !in range) {
        return Err(InvocationError.TableGrowExceedsLimits(proposedLength))
    }

    repeat(elementsToAdd) {
        elements.add(initialisationValue)
    }

    val newLimits = type.limits.copy(min = proposedLength)

    return Ok(copy(type.copy(limits = newLimits)))
}

inline fun TableInstance.element(
    elementIndex: Int,
): Result<ReferenceValue, InvocationError.TableElementLookupFailed> = elements.getOrNull(elementIndex).toResultOr {
    InvocationError.TableElementLookupFailed(elementIndex)
}
