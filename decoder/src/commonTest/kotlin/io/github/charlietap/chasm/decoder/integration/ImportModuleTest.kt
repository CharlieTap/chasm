package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionCompositeType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportModuleTest {

    @Test
    fun `can decode an import module section`() {

        val byteStream = Resource("src/commonTest/resources/import.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(emptyList()),
            results = resultType(listOf(i32ValueType())),
        )
        val expectedRecursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(
                    heapTypes = emptyList(),
                    compositeType = functionCompositeType(expectedFunctionType),
                ),
            ),
            state = RecursiveType.State.SYNTAX,
        )

        val expectedType = type(
            idx = typeIndex(0u),
            recursiveType = expectedRecursiveType,
        )
        val expectedDefinedType = definedType(
            expectedRecursiveType.copy(
                state = RecursiveType.State.CLOSED,
            ),
        )

        val expectedFunctionImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalFunction"),
            descriptor = Import.Descriptor.Function(expectedDefinedType),
        )

        val expectedTableType = TableType(ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u, 2u))

        val expectedTableImport = Import(
            moduleName = NameValue("env"),
            entityName = NameValue("externalTable"),
            descriptor = Import.Descriptor.Table(expectedTableType),
        )

        val expectedMemoryType = memoryType(limits(1u, 2u))

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
                types = listOf(expectedType),
                definedTypes = listOf(expectedDefinedType),
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

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
