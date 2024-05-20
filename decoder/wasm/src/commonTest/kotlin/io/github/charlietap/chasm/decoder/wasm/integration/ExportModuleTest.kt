package io.github.charlietap.chasm.decoder.wasm.integration

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
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportModuleTest {

    @Test
    fun `can decode an export module section`() {

        val byteStream = Resource("src/commonTest/resources/export.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(emptyList()),
            results = resultType(emptyList()),
        )
        val expectedRecursiveType = RecursiveType(
            listOf(
                SubType.Final(emptyList(), CompositeType.Function(expectedFunctionType)),
            ),
        )

        val expectedFunctionExportType = Type(Index.TypeIndex(0u), expectedRecursiveType)

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

        val expectedTableType = TableType(ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u))
        val expectedTable = Table(
            idx = Index.TableIndex(0u),
            type = expectedTableType,
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(AbstractHeapType.Func))),
        )

        val expectedTableExport = Export(
            name = NameValue("table"),
            descriptor = Export.Descriptor.Table(Index.TableIndex(0u)),
        )

        val expectedMemoryType = MemoryType(Limits(1u))
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
            Module(
                version = Version.One,
                types = listOf(expectedFunctionExportType),
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

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
