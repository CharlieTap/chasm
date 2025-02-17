package io.github.charlietap.chasm.type.ir.rolling.substitution

typealias TypeSubstitutor<T> = (T, ConcreteHeapTypeSubstitutor) -> T
