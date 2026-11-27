package io.github.charlietap.chasm.runtime.ext

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.value.ReferenceValue

@InternalChasmApi
inline fun Int.wrapI31(): ReferenceValue.I31 {
    return ReferenceValue.I31(this.toUInt() and 0x7FFFFFFFu)
}
