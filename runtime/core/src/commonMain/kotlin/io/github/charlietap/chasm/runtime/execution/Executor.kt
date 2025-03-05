package io.github.charlietap.chasm.runtime.execution

typealias Executor<T> = (ExecutionContext, T) -> Unit
