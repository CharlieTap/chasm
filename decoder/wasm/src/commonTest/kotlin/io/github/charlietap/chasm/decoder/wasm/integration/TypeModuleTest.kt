package io.github.charlietap.chasm.decoder.wasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.type.recursiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeModuleTest {

    @Test
    fun `can decode a type module section`() {

        val byteStream = Resource("src/commonTest/resources/type.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

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
        val expectedNumberTypeRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Function(expectedNumberTypeFunctionType),
                ),
            ),
        )
        val expectedNumberType = Type(Index.TypeIndex(0u), expectedNumberTypeRecursiveType)

        val expectedVectorTypeFunctionType = FunctionType(
            params = ResultType(
                listOf(
                    ValueType.Vector(VectorType.V128),
                ),
            ),
            results = ResultType(listOf(ValueType.Vector(VectorType.V128))),
        )
        val expectedVectorTypeRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Function(expectedVectorTypeFunctionType),
                ),
            ),
        )
        val expectedVectorType = Type(Index.TypeIndex(1u), expectedVectorTypeRecursiveType)

        val expectedReferenceTypeFunctionType = FunctionType(
            params = ResultType(
                listOf(
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)),
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern)),
                ),
            ),
            results = ResultType(listOf(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)))),
        )
        val expectedReferenceTypeRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Function(expectedReferenceTypeFunctionType),
                ),
            ),
        )
        val expectedReferenceType = Type(Index.TypeIndex(2u), expectedReferenceTypeRecursiveType)

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

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
