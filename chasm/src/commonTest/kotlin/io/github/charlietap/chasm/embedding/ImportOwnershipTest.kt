package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.MEMORY_IMPORT_MODULE
import io.github.charlietap.chasm.embedding.fixture.decodedModule
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostFunction
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ImportOwnershipTest {

    @Test
    fun `importables from another store are rejected before instantiation`() = runTest {

        val sourceStore = interpreterStore()
        val targetRuntime = store()
        val targetStore = interpreterStore(targetRuntime)
        val module = decodedModule(MEMORY_IMPORT_MODULE)
        val importables = listOf(
            function(sourceStore, functionType(), HostFunction { _, _ -> }),
            global(sourceStore, globalType(), NumberValue.I32(0)),
            memory(sourceStore, memoryType(limits = limits(min = 1u, max = 1u))),
            table(sourceStore, tableType(referenceType = refNullReferenceType()), null),
            tag(sourceStore, tagType()),
        )

        importables.forEach { importable ->

            val failure = assertFailsWith<IllegalArgumentException> {
                instance(targetStore, module, listOf(Import("env", "memory", importable)))
            }

            assertEquals("the import env.memory belongs to a different store", failure.message)
        }
        assertEquals(0, targetRuntime.memories.size)
    }
}
