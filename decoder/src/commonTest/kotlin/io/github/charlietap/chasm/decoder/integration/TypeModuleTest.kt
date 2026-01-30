package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.f64ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeModuleTest {

    @Test
    fun `can decode a type module section`() {

        val byteStream = Resource("type.wasm").readBytes()

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
        val expectedNumberDefinedType = definedType(expectedNumberTypeRecursiveType.copy(state = RecursiveType.State.CLOSED))

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
        val expectedVectorDefinedType = definedType(expectedVectorTypeRecursiveType.copy(state = RecursiveType.State.CLOSED))

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
        val expectedReferenceDefinedType = definedType(expectedReferenceTypeRecursiveType.copy(state = RecursiveType.State.CLOSED))

        val expected = Ok(
            module(
                version = Version.One,
                types = listOf(
                    expectedNumberType,
                    expectedVectorType,
                    expectedReferenceType,
                ),
                definedTypes = listOf(
                    expectedNumberDefinedType,
                    expectedVectorDefinedType,
                    expectedReferenceDefinedType,
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
