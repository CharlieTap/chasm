package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportModuleTest {

    @Test
    fun `can decode an import module section`() {

        val byteStream = Resource("src/commonTest/resources/import.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(emptyList()),
            results = resultType(listOf(i32ValueType())),
        )
        val expectedRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Function(expectedFunctionType),
                ),
            ),
        )
        val expectedFunctionImportType = Type(Index.TypeIndex(0u), expectedRecursiveType)

        val expectedFunctionImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalFunction"),
            descriptor = Import.Descriptor.Function(expectedFunctionType),
        )

        val expectedTableType = TableType(ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u, 2u))

        val expectedTableImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalTable"),
            descriptor = Import.Descriptor.Table(expectedTableType),
        )

        val expectedMemoryType = MemoryType(Limits(1u, 2u))

        val expectedMemoryImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalMemory"),
            descriptor = Import.Descriptor.Memory(expectedMemoryType),
        )

        val expectedGlobalType = GlobalType(
            i32ValueType(),
            Mutability.Const,
        )

        val expectedGlobalImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalGlobal"),
            descriptor = Import.Descriptor.Global(expectedGlobalType),
        )

        val expected = Ok(
            module(
                version = Version.One,
                types = listOf(expectedFunctionImportType),
                imports = listOf(
                    expectedFunctionImport,
                    expectedTableImport,
                    expectedMemoryImport,
                    expectedGlobalImport,
                ),
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
