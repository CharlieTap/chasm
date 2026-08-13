package io.github.charlietap.chasm.compiler

fun interface ParallelTaskScope {

    fun ensureActive()
}

interface ParallelTaskExecutor {

    suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T>
}
