package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.type.f32ValueType
import io.github.charlietap.chasm.fixture.ast.type.f64ValueType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.recursiveType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeModuleTest {

    @Test
    fun `can decode a type module section`() {

        val byteStream = Resource("src/commonTest/resources/type.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedNumberTypeFunctionType = FunctionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                    f32ValueType(),
                    f64ValueType(),
                ),
            ),
            results = resultType(listOf(i32ValueType())),
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
            params = resultType(
                listOf(
                    ValueType.Vector(VectorType.V128),
                ),
            ),
            results = resultType(listOf(ValueType.Vector(VectorType.V128))),
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
            params = resultType(
                listOf(
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)),
                    ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern)),
                ),
            ),
            results = resultType(listOf(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func)))),
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
            module(
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

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
