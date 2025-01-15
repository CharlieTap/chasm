package io.github.charlietap.chasm.executor.runtime.execution

typealias Executor<T> = (ExecutionContext, T) -> Unit
