package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.i32AddressType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportModuleTest {

    @Test
    fun `can decode an export module section`() {

        val byteStream = Resource("export.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(emptyList()),
            results = resultType(emptyList()),
        )
        val expectedRecursiveType = recursiveType(
            subTypes = listOf(
                SubType.Final(emptyList(), CompositeType.Function(expectedFunctionType)),
            ),
            state = RecursiveType.State.SYNTAX,
        )

        val expectedFunctionExportType = Type(Index.TypeIndex(0u), expectedRecursiveType)
        val expectedDefinedType = definedType(
            expectedRecursiveType.copy(state = RecursiveType.State.CLOSED),
        )

        val expectedFunction = Function(
            idx = Index.FunctionIndex(0u),
            typeIndex = Index.TypeIndex(0u),
            locals = emptyList(),
            body = Expression(emptyList()),
        )

        val expectedFunctionExport = Export(
            name = NameValue("function"),
            descriptor = Export.Descriptor.Function(Index.FunctionIndex(0u)),
        )

        val expectedTableType = TableType(i32AddressType(), ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u))
        val expectedTable = Table(
            idx = Index.TableIndex(0u),
            type = expectedTableType,
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(AbstractHeapType.Func))),
        )

        val expectedTableExport = Export(
            name = NameValue("table"),
            descriptor = Export.Descriptor.Table(Index.TableIndex(0u)),
        )

        val expectedMemoryType = memoryType(i32AddressType(), limits(1u))
        val expectedMemory = Memory(
            idx = Index.MemoryIndex(0u),
            type = expectedMemoryType,
        )

        val expectedMemoryExport = Export(
            name = NameValue("memory"),
            descriptor = Export.Descriptor.Memory(Index.MemoryIndex(0u)),
        )

        val expectedGlobalType = GlobalType(
            i32ValueType(),
            Mutability.Var,
        )

        val expectedGlobal = Global(
            idx = Index.GlobalIndex(0u),
            type = expectedGlobalType,
            initExpression = Expression(listOf(NumericInstruction.I32Const(0))),
        )

        val expectedGlobalImport = Export(
            name = NameValue("global"),
            descriptor = Export.Descriptor.Global(Index.GlobalIndex(0u)),
        )

        val expected = Ok(
            module(
                version = Version.One,
                types = listOf(expectedFunctionExportType),
                definedTypes = listOf(expectedDefinedType),
                imports = emptyList(),
                functions = listOf(expectedFunction),
                tables = listOf(expectedTable),
                memories = listOf(expectedMemory),
                globals = listOf(expectedGlobal),
                exports = listOf(
                    expectedFunctionExport,
                    expectedTableExport,
                    expectedMemoryExport,
                    expectedGlobalImport,
                ),
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
