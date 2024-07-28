package io.github.charlietap.chasm.decoder.wasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionModuleTest {

    @Test
    fun `can decode a function module section`() {

        val byteStream = Resource("src/commonTest/resources/function.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i32ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )
        val expectedRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Function(expectedFunctionType),
                ),
            ),
        )
        val expectedType = Type(Index.TypeIndex(0u), expectedRecursiveType)

        val expectedFunction = Function(
            idx = Index.FunctionIndex(0u),
            typeIndex = Index.TypeIndex(0u),
            locals = listOf(
                Local(Index.LocalIndex(0u), i32ValueType()),
                Local(Index.LocalIndex(1u), i32ValueType()),
                Local(Index.LocalIndex(2u), i32ValueType()),
                Local(Index.LocalIndex(3u), i64ValueType()),
                Local(Index.LocalIndex(4u), f32ValueType()),
            ),
            body = Expression(
                listOf(
                    NumericInstruction.I32Const(0),
                    NumericInstruction.I64Const(0),
                ),
            ),
        )

        val expected = Ok(
            Module(
                version = Version.One,
                types = listOf(expectedType),
                imports = emptyList(),
                functions = listOf(expectedFunction),
                tables = emptyList(),
                memories = emptyList(),
                globals = emptyList(),
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
