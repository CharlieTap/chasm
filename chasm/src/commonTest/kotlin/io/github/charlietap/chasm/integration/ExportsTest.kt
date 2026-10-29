package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportsTest {

    @Test
    fun `can instantiate a module and return its exports`() {

        val byteStream = Resource(FILE_DIR + "export.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)
        val store = store()

        val actual = module(reader)
            .flatMap { module ->
                instance(store, module, emptyList())
            }.map { instance ->
                exports(instance).map { export -> export.name to export.value::class }
            }.getOrNull()

        val expected = listOf(
            "exported_function" to Function::class,
            "exported_global" to Global::class,
            "exported_memory" to Memory::class,
            "exported_table" to Table::class,
            "exported_tag" to Tag::class,
        )

        assertEquals(expected, actual)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
