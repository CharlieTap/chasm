package io.github.charlietap.chasm.executor.memory

typealias BoundsChecker<T> = (Int, Int, Int, () -> T) -> T
