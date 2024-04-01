package io.github.charlietap.chasm.executor.type.rolling.substitution

typealias TypeSubstitutor<T> = (T, ConcreteHeapTypeSubstitutor) -> T
