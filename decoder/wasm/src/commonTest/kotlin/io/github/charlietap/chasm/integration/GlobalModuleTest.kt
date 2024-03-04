package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.WasmModuleDecoder
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalModuleTest {

    @Test
    fun `can decode a global module section`() {

        val byteStream = Resource("src/commonTest/resources/global.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)
        val decoder = WasmModuleDecoder()

        val expectedConstGlobal = Global(
            idx = Index.GlobalIndex(0u),
            type = GlobalType(
                valueType = ValueType.Number(NumberType.I32),
                GlobalType.Mutability.Const,
            ),
            initExpression = Expression(
                listOf(NumericInstruction.I32Const(10)),
            ),
        )

        val expectedVarGlobal = Global(
            idx = Index.GlobalIndex(1u),
            type = GlobalType(
                valueType = ValueType.Number(NumberType.I32),
                GlobalType.Mutability.Var,
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

        val actual = decoder(reader)

        assertEquals(expected, actual)
    }
}
