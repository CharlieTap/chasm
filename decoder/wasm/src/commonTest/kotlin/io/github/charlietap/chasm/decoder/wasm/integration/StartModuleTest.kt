package io.github.charlietap.chasm.decoder.wasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class StartModuleTest {

    @Test
    fun `can decode a start module section`() {

        val byteStream = Resource("src/commonTest/resources/start.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        val expectedType = Type(Index.TypeIndex(0u), expectedFunctionType)

        val expectedFunction = Function(
            idx = Index.FunctionIndex(0u),
            typeIndex = Index.TypeIndex(0u),
            locals = emptyList(),
            body = Expression(emptyList()),
        )

        val startFunction = StartFunction(Index.FunctionIndex(0u))

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
                startFunction = startFunction,
                elementSegments = emptyList(),
                dataSegments = emptyList(),
                customs = emptyList(),
            ),
        )

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
