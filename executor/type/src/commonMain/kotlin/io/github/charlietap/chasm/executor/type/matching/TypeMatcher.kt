package io.github.charlietap.chasm.executor.type.matching

typealias TypeMatcher<T> = (T, T, TypeMatcherContext) -> Boolean
