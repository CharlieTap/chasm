package io.github.charlietap.chasm.decoder.wasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.type.i32ValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalModuleTest {

    @Test
    fun `can decode a global module section`() {

        val byteStream = Resource("src/commonTest/resources/global.wasm").readBytes()

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
            Module(
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

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
