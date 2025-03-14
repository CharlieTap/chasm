package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalModuleTest {

    @Test
    fun `can decode a global module section`() {

        val byteStream = Resource("src/commonTest/resources/global.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedConstGlobal = Global(
            idx = Index.GlobalIndex(0u),
            type = GlobalType(
                valueType = i32ValueType(),
                Mutability.Const,
            ),
            initExpression = Expression(
                listOf(NumericInstruction.I32Const(10)),
            ),
        )

        val expectedVarGlobal = Global(
            idx = Index.GlobalIndex(1u),
            type = GlobalType(
                valueType = i32ValueType(),
                Mutability.Var,
            ),
            initExpression = Expression(
                listOf(NumericInstruction.I32Const(20)),
            ),
        )

        val expected = Ok(
            module(
                version = Version.One,
                types = emptyList(),
                imports = emptyList(),
                functions = emptyList(),
                tables = emptyList(),
                memories = emptyList(),
                globals = listOf(
                    expectedConstGlobal,
                    expectedVarGlobal,
                ),
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
