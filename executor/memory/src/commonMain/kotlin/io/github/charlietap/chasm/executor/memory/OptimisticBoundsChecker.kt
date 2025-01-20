package io.github.charlietap.chasm.executor.memory

/*
    On the JVM we perform no bounds check and simply set up a try catch
    whereas on native platforms we always perform bounds checks
 */
expect inline fun <T> OptimisticBoundsChecker(
    address: Int,
    bytes: Int,
    memoryUpperBound: Int,
    crossinline operation: () -> T,
): T
