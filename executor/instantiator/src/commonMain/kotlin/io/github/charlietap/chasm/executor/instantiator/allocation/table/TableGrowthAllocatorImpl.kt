package io.github.charlietap.chasm.executor.instantiator.allocation.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun TableGrowthAllocatorAllocatorImpl(
    tableInstance: TableInstance,
    elementsToAdd: UInt,
    reference: ReferenceValue,
): Result<TableInstance, InvocationError.TableGrowExceedsLimits> {

    val currentLength = tableInstance.elements.size
    val newLength = currentLength.toUInt() + elementsToAdd

    tableInstance.type.limits.max?.let { max ->
        if (newLength > max) {
            return Err(InvocationError.TableGrowExceedsLimits(max))
        }
    }

    val newType = tableInstance.type.copy(
        limits = tableInstance.type.limits.copy(min = newLength),
    )

    val newElements = tableInstance.elements + List(elementsToAdd.toInt()) {
        reference
    }

    return Ok(TableInstance(newType, newElements.toMutableList()))
}
