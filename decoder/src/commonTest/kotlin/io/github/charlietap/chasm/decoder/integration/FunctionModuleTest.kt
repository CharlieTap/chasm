package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionModuleTest {

    @Test
    fun `can decode a function module section`() {

        val byteStream = Resource("function.wasm").readBytes()

        val config = moduleConfig()
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
        val expectedDefinedType = definedType(
            expectedRecursiveType.copy(state = RecursiveType.State.CLOSED),
        )

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
            module(
                version = Version.One,
                types = listOf(expectedType),
                definedTypes = listOf(expectedDefinedType),
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

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
