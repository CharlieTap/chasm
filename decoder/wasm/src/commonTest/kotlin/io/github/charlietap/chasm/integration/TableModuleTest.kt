package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.WasmModuleDecoder
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class TableModuleTest {

    @Test
    fun `can decode a function module section`() {

        val byteStream = Resource("src/commonTest/resources/table.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)
        val decoder = WasmModuleDecoder()

        val expectedTable = Table(
            idx = Index.TableIndex(0u),
            type = TableType(
                referenceType = ReferenceType.Funcref,
                limits = Limits(1u, 2u),
            ),
        )

        val expected = Ok(
            Module(
                version = Version.One,
                types = emptyList(),
                imports = emptyList(),
                functions = emptyList(),
                tables = listOf(expectedTable),
                memories = emptyList(),
                globals = emptyList(),
                exports = emptyList(),
                startFunction = null,
                elementSegments = emptyList(),
                dataSegments = emptyList(),
                customs = emptyList(),
            ),
        )

        val actual = decoder(reader)

        assertEquals(expected, actual)
    }
}
