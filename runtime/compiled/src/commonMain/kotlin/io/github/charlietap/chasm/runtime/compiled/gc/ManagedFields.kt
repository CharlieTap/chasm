@file:JvmName("ManagedFields")

package io.github.charlietap.chasm.runtime.compiled.gc

import kotlin.jvm.JvmName

fun fieldIndexError(index: Int): IndexOutOfBoundsException {
    return IndexOutOfBoundsException("field $index does not exist")
}

fun referenceFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a reference")
}

fun numberFieldError(index: Int): IllegalArgumentException {
    return IllegalArgumentException("field $index holds a number")
}

fun ownerCollectedError(): IllegalStateException {
    return IllegalStateException("the store of this reference is no longer alive")
}
