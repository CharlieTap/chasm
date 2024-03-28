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
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportModuleTest {

    @Test
    fun `can decode an export module section`() {

        val byteStream = Resource("src/commonTest/resources/export.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        val expectedFunctionExportType = Type(Index.TypeIndex(0u), expectedFunctionType)

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

        val expectedTableType = TableType(ReferenceType.RefNull(HeapType.Func), Limits(1u))
        val expectedTable = Table(
            idx = Index.TableIndex(0u),
            type = expectedTableType,
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(HeapType.Func))),
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
            ValueType.Number(NumberType.I32),
            GlobalType.Mutability.Var,
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
