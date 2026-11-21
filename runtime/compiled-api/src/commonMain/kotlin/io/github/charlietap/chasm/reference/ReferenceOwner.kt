package io.github.charlietap.chasm.reference

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
interface ReferenceOwner {

    fun owner(): Any
}
