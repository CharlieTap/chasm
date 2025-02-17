package io.github.charlietap.chasm.type.ir.matching

typealias TypeMatcher<T> = (T, T, TypeMatcherContext) -> Boolean
