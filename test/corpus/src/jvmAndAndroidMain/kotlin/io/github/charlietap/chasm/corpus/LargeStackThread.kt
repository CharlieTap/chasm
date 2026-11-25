package io.github.charlietap.chasm.corpus

import io.github.charlietap.chasm.runtime.execution.CompiledStack

class LargeStackThread : FixtureThread {

    override fun <T> run(body: () -> T): T {

        var outcome: Result<T>? = null
        val thread = Thread(null, { outcome = runCatching(body) }, THREAD_NAME, STACK_BYTES)
        val previousBudgetSource = CompiledStack.budgetSource

        CompiledStack.budgetSource = { CompiledStack.budgetFor(STACK_BYTES) }
        try {
            thread.start()
            thread.join()
        } finally {
            CompiledStack.budgetSource = previousBudgetSource
        }

        return checkNotNull(outcome).getOrThrow()
    }

    private companion object {
        const val THREAD_NAME = "corpus"
        const val STACK_BYTES = 32L shl 20
    }
}
