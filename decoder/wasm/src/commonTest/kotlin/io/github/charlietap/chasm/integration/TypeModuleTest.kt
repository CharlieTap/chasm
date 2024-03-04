package io.github.charlietap.chasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.WasmModuleDecoder
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeModuleTest {

    @Test
    fun `can decode a type module section`() {

        val byteStream = Resource("src/commonTest/resources/type.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)
        val decoder = WasmModuleDecoder()

        val expectedNumberTypeFunctionType = FunctionType(
            params = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                    ValueType.Number(NumberType.F32),
                    ValueType.Number(NumberType.F64),
                ),
            ),
            results = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        val expectedNumberType = Type(Index.TypeIndex(0u), expectedNumberTypeFunctionType)

        val expectedVectorTypeFunctionType = FunctionType(
            params = ResultType(
                listOf(
                    ValueType.Vector(VectorType.V128),
                ),
            ),
            results = ResultType(listOf(ValueType.Vector(VectorType.V128))),
        )
        val expectedVectorType = Type(Index.TypeIndex(1u), expectedVectorTypeFunctionType)

        val expectedReferenceTypeFunctionType = FunctionType(
            params = ResultType(
                listOf(
                    ValueType.Reference(ReferenceType.Funcref),
                    ValueType.Reference(ReferenceType.Externref),
                ),
            ),
            results = ResultType(listOf(ValueType.Reference(ReferenceType.Funcref))),
        )
        val expectedReferenceType = Type(Index.TypeIndex(2u), expectedReferenceTypeFunctionType)

        val expected = Ok(
            Module(
                version = Version.One,
                types = listOf(
                    expectedNumberType,
                    expectedVectorType,
                    expectedReferenceType,
                ),
                imports = emptyList(),
                functions = emptyList(),
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

        val actual = decoder(reader)

        assertEquals(expected, actual)
    }
}
