package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.fixture.publicExport
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.instance.tagAddress
import io.github.charlietap.chasm.fixture.instance.tagExternalValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportsTest {

    @Test
    fun `can instantiate a module and return its exports`() {

        val byteStream = Resource(FILE_DIR + "export.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val actual = module(reader).flatMap { module ->
            instance(publicStore(), module, emptyList())
        }.map { instance ->
            exports(instance)
        }.getOrNull()

        val expected = listOf(
            publicExport(
                name = "exported_function",
                value = publicFunction(
                    reference = functionExternalValue(
                        address = functionAddress(0),
                    ),
                ),
            ),
            publicExport(
                name = "exported_global",
                value = publicGlobal(
                    reference = globalExternalValue(
                        address = globalAddress(0),
                    ),
                ),
            ),
            publicExport(
                name = "exported_memory",
                value = publicMemory(
                    reference = memoryExternalValue(
                        address = memoryAddress(0),
                    ),
                ),
            ),
            publicExport(
                name = "exported_table",
                value = publicTable(
                    reference = tableExternalValue(
                        address = tableAddress(0),
                    ),
                ),
            ),
            publicExport(
                name = "exported_tag",
                value = publicTag(
                    reference = tagExternalValue(
                        address = tagAddress(0),
                    ),
                ),
            ),
        )

        assertEquals(expected, actual)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
