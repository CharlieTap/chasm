@file:JvmName("ManagedFields")

package io.github.charlietap.chasm.runtime.compiled.gc

import kotlin.jvm.JvmName

internal fun fieldIndexError(index: Int): IndexOutOfBoundsException {
    return IndexOutOfBoundsException("field $index does not exist")
}

internal fun referenceFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a reference")
}

internal fun numberFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a number")
}

internal fun laneError(lane: Int): IndexOutOfBoundsException {
    return IndexOutOfBoundsException("lane $lane does not exist")
}
