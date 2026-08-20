package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicModule
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val mapper = object : Mapper<Importable, ExternalValue> {
            override fun map(input: Importable): ExternalValue = error("mapping must not start")
        }
        val expected = ChasmResult.Error(
            ChasmError.ExecutionError("Importable belongs to a different Store"),
        )

        importables.forEach { importable ->
            val actual = instance(
                store = targetStore,
                module = publicModule(),
                imports = listOf(publicImport(value = importable)),
                config = RuntimeConfig(),
                instantiator = { _, _, _, _ -> error("instantiation must not start") },
                importableMapper = mapper,
            )

            assertEquals(expected, actual)
        }
    }
}
