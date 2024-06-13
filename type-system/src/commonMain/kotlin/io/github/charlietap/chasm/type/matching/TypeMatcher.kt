package io.github.charlietap.chasm.type.matching

typealias TypeMatcher<T> = (T, T, TypeMatcherContext) -> Boolean
