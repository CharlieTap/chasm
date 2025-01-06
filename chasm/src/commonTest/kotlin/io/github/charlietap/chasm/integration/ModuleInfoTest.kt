package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.fixture.publicMemoryType
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.ExternalType
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleInfoTest {

    @Test
    fun `can return module info for all import and export types`() {

        val importedFunctionType = FunctionType(
            listOf(ValueType.Number.I32),
            emptyList(),
        )
        val exportedFunctionType = FunctionType(
            emptyList(),
            emptyList(),
        )

        val importedGlobalType = GlobalType(ValueType.Number.I32, Mutability.Const)
        val exportedGlobalType = GlobalType(ValueType.Number.I32, Mutability.Variable)

        val importedMemoryType = publicMemoryType(Limits(1u))
        val exportedMemoryType = publicMemoryType(Limits(2u))

        val importedTableType = TableType(Limits(1u), ValueType.Reference.RefNull(AbstractHeapType.Func))
        val exportedTableType = TableType(Limits(2u), ValueType.Reference.RefNull(AbstractHeapType.Func))

        val importedTagFunctionType = FunctionType(listOf(ValueType.Number.I32), emptyList())
        val importedTagType = TagType(TagType.Attribute.Exception, importedTagFunctionType)
        val exportedTagFunctionType = FunctionType(listOf(ValueType.Number.I64), emptyList())
        val exportedTagType = TagType(TagType.Attribute.Exception, exportedTagFunctionType)

        val byteStream = Resource(FILE_DIR + "module_info.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val actual = module(reader)
            .map { module ->
                moduleInfo(module)
            }.getOrNull()

        val expected = ModuleInfo(
            imports = listOf(
                ImportDefinition(
                    moduleName = "env",
                    entityName = "imported_function",
                    type = ExternalType.Function(importedFunctionType),
                ),
                ImportDefinition(
                    moduleName = "env",
                    entityName = "imported_global",
                    type = ExternalType.Global(importedGlobalType),
                ),
                ImportDefinition(
                    moduleName = "env",
                    entityName = "imported_memory",
                    type = ExternalType.Memory(importedMemoryType),
                ),
                ImportDefinition(
                    moduleName = "env",
                    entityName = "imported_table",
                    type = ExternalType.Table(importedTableType),
                ),
                ImportDefinition(
                    moduleName = "env",
                    entityName = "imported_tag",
                    type = ExternalType.Tag(importedTagType),
                ),
            ),
            exports = listOf(
                ExportDefinition(
                    name = "exported_function",
                    type = ExternalType.Function(exportedFunctionType),
                ),
                ExportDefinition(
                    name = "exported_global",
                    type = ExternalType.Global(exportedGlobalType),
                ),
                ExportDefinition(
                    name = "exported_memory",
                    type = ExternalType.Memory(exportedMemoryType),
                ),
                ExportDefinition(
                    name = "exported_table",
                    type = ExternalType.Table(exportedTableType),
                ),
                ExportDefinition(
                    name = "exported_tag",
                    type = ExternalType.Tag(exportedTagType),
                ),
            ),
        )

        assertEquals(expected, actual)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
