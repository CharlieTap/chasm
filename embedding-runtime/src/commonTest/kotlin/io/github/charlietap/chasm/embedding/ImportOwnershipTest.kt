package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.embedding.internal.instantiate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ImportOwnershipTest {

    @Test
    fun `importables from another store are rejected before instantiation`() {

        val sourceStore = publicStore()
        val targetStore = publicStore()
        val importables = listOf(
            publicFunction(store = sourceStore.store),
            publicGlobal(store = sourceStore.store),
            publicMemory(store = sourceStore.store),
            publicTable(store = sourceStore.store),
            publicTag(store = sourceStore.store),
        )

        importables.forEach { importable ->

            val failure = assertFailsWith<IllegalArgumentException> {
                instantiate(
                    store = targetStore,
                    imports = listOf(publicImport(value = importable)),
                    config = RuntimeConfig(),
                    trapSource = { null },
                ) { _, _, _ -> error("instantiation must not start") }
            }

            assertEquals("Importable belongs to a different Store", failure.message)
        }
    }
}
