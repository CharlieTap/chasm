package io.github.charlietap.chasm.parallel

fun interface ParallelTaskScope {

    fun ensureActive()
}

interface ParallelTaskExecutor {

    /** Executes [tasks] and returns their results in the same order. */
    suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T>
}
