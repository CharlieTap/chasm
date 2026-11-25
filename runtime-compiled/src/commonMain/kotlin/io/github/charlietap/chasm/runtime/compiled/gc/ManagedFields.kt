@file:JvmName("ManagedFields")

package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmName

@InternalChasmApi
fun fieldIndexError(index: Int): IndexOutOfBoundsException {
    return IndexOutOfBoundsException("field $index does not exist")
}

@InternalChasmApi
fun referenceFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a reference")
}

@InternalChasmApi
fun numberFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a number")
}

@InternalChasmApi
fun laneError(lane: Int): IndexOutOfBoundsException {
    return IndexOutOfBoundsException("lane $lane does not exist")
}
