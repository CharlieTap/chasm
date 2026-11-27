package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.InternalChasmApi

/*
    On the JVM we perform no bounds check and simply set up a try catch
    whereas on native platforms we always perform bounds checks
 */
@InternalChasmApi
expect inline fun <T> OptimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): T
