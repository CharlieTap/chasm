package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import kotlin.test.Test
import kotlin.test.assertEquals

class TableModuleTest {

    @Test
    fun `can decode a table module section`() {

        val byteStream = Resource("src/commonTest/resources/table.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedTable = Table(
            idx = Index.TableIndex(0u),
            type = TableType(
                referenceType = ReferenceType.RefNull(AbstractHeapType.Func),
                limits = Limits(1u, 2u),
            ),
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(AbstractHeapType.Func))),
        )

        val expected = Ok(
            module(
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

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
