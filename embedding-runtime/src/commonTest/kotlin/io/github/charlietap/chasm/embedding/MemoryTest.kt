package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.internal.InterpreterEngine
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    @Test
    fun `can allocate a memory in the store and return a handle`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = memoryType(limits = limits(min = 1u))

        val actual = memory(store, type)

        assertEquals(type, actual.type)
        assertEquals(memoryType(limits = limits(min = 1u)), runtime.memories[0].type)
        assertEquals(PAGE_SIZE, runtime.memories[0].size)
    }

    @Test
    fun `an interpreter memory can be allocated with a linear memory config`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val engine = store._engine() as InterpreterEngine
        val type = memoryType(limits = limits(min = 1u))

        val actual = engine.memory(type, LinearMemoryConfig(prefault = true))

        assertEquals(type, actual.type)
        assertEquals(PAGE_SIZE, runtime.memories.single().size)
    }
}
