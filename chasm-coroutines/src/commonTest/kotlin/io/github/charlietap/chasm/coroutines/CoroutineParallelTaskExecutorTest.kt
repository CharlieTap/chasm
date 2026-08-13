package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.compiler.ParallelTaskScope
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutineParallelTaskExecutorTest {

    @Test
    fun `executes tasks and preserves their order`() = runTest {
        val tasks: List<ParallelTaskScope.() -> Int> = List(4) { index ->
            {
                ensureActive()
                index
            }
        }

        val results = CoroutineParallelTaskExecutor.execute(tasks)

        assertEquals(listOf(0, 1, 2, 3), results)
    }

    @Test
    fun `instantiates a module through the coroutine API`() = runTest {
        val module = module(EMPTY_MODULE).expect("expected module to decode")

        instance(
            store = store(),
            module = module,
            imports = emptyList(),
        ).expect("expected module to instantiate")
    }

    private companion object {
        val EMPTY_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
        )
    }
}
