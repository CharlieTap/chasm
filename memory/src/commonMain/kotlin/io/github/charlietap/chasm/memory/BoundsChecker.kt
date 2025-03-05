package io.github.charlietap.chasm.memory

typealias BoundsChecker<T> = (Int, Int, Int, () -> T) -> T
