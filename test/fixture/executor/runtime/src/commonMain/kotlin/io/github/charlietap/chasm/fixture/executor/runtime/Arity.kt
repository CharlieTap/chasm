package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Arity

fun arity(value: Int = 0) = argumentArity(value)

fun argumentArity(value: Int = 0) = Arity.Argument(value)

fun returnArity(value: Int = 0) = Arity.Return(value)
