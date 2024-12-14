package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

inline fun TableInstance.element(
    elementIndex: Int,
): Result<ReferenceValue, InvocationError.TableElementLookupFailed> = elements.getOrNull(elementIndex).toResultOr {
    InvocationError.TableElementLookupFailed(elementIndex)
}
