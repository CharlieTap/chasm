package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.embedding.fixture.publicModule
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.functionImport
import io.github.charlietap.chasm.fixture.ast.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.globalExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tagExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tagImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.chasm.embedding.exportDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.functionNameData
import io.github.charlietap.chasm.fixture.chasm.embedding.importDefinition
import io.github.charlietap.chasm.fixture.chasm.embedding.nameData
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fixture.runtime.type.functionExternalType
import io.github.charlietap.chasm.fixture.runtime.type.globalExternalType
import io.github.charlietap.chasm.fixture.runtime.type.memoryExternalType
import io.github.charlietap.chasm.fixture.runtime.type.tableExternalType
import io.github.charlietap.chasm.fixture.runtime.type.tagExternalType
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.exceptionAttribute
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32AddressType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.RecursiveType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.ast.module.module as astModule

class ModuleInfoTest {

    @Test
    fun `can return module info for imports that are re-exported`() {

        val importedFunctionType = functionType(
            params = resultType(listOf(i32ValueType())),
        )
        val importedGlobalType = globalType(i32ValueType(), constMutability())
        val importedMemoryType = memoryType(i32AddressType(), limits(1u))
        val importedTableType = tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), limits(1u))
        val importedTagType = tagType(exceptionAttribute(), 0, importedFunctionType)

        val internalModule = astModule(
            types = listOf(
                type(
                    idx = typeIndex(0u),
                    recursiveType = functionRecursiveType(importedFunctionType),
                ),
            ),
            definedTypes = listOf(
                definedType(
                    recursiveType = functionRecursiveType(importedFunctionType),
                    typeIndex = 0,
                ),
            ),
            imports = listOf(
                functionImport(
                    moduleName = NameValue("env"),
                    entityName = NameValue("imported_function"),
                    descriptor = functionImportDescriptor(typeIndex(0u)),
                ),
                import(
                    moduleName = NameValue("env"),
                    entityName = NameValue("imported_global"),
                    descriptor = globalImportDescriptor(importedGlobalType),
                ),
                import(
                    moduleName = NameValue("env"),
                    entityName = NameValue("imported_memory"),
                    descriptor = memoryImportDescriptor(importedMemoryType),
                ),
                import(
                    moduleName = NameValue("env"),
                    entityName = NameValue("imported_table"),
                    descriptor = tableImportDescriptor(importedTableType),
                ),
                import(
                    moduleName = NameValue("env"),
                    entityName = NameValue("imported_tag"),
                    descriptor = tagImportDescriptor(importedTagType),
                ),
            ),
            exports = listOf(
                export(
                    name = NameValue("reexported_function"),
                    descriptor = functionExportDescriptor(functionIndex(0u)),
                ),
                export(
                    name = NameValue("reexported_global"),
                    descriptor = globalExportDescriptor(),
                ),
                export(
                    name = NameValue("reexported_memory"),
                    descriptor = memoryExportDescriptor(),
                ),
                export(
                    name = NameValue("reexported_table"),
                    descriptor = tableExportDescriptor(),
                ),
                export(
                    name = NameValue("reexported_tag"),
                    descriptor = tagExportDescriptor(),
                ),
            ),
        )

        val actual = moduleInfo(
            publicModule(module = internalModule),
        )

        val expected = ModuleInfo(
            imports = listOf(
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_function",
                    type = functionExternalType(importedFunctionType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_global",
                    type = globalExternalType(importedGlobalType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_memory",
                    type = memoryExternalType(importedMemoryType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_table",
                    type = tableExternalType(importedTableType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_tag",
                    type = tagExternalType(importedTagType),
                ),
            ),
            exports = listOf(
                exportDefinition(
                    name = "reexported_function",
                    type = functionExternalType(importedFunctionType),
                ),
                exportDefinition(
                    name = "reexported_global",
                    type = globalExternalType(importedGlobalType),
                ),
                exportDefinition(
                    name = "reexported_memory",
                    type = memoryExternalType(importedMemoryType),
                ),
                exportDefinition(
                    name = "reexported_table",
                    type = tableExternalType(importedTableType),
                ),
                exportDefinition(
                    name = "reexported_tag",
                    type = tagExternalType(importedTagType),
                ),
            ),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can return module info for all import and export types`() {

        val importedFunctionType = functionType(
            params = resultType(listOf(i32ValueType())),
        )
        val exportedFunctionType = functionType(
            params = resultType(
                types = listOf(
                    i32ValueType(),
                    i32ValueType(),
                ),
            ),
        )
        val functionNameData = functionNameData(
            name = "exported_function_name",
            localNames = listOf("a", "b"),
        )

        val importedGlobalType = globalType(i32ValueType(), constMutability())
        val exportedGlobalType = globalType(i32ValueType(), varMutability())

        val importedMemoryType = memoryType(i32AddressType(), limits(1u))
        val exportedMemoryType = memoryType(i32AddressType(), limits(2u))

        val importedTableType = tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), limits(1u))
        val exportedTableType = tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), limits(2u))

        val importedTagFunctionType = functionType(
            params = resultType(listOf(i32ValueType())),
        )
        val importedTagType = tagType(exceptionAttribute(), 0, importedTagFunctionType)
        val exportedTagFunctionType = functionType(
            params = resultType(listOf(i64ValueType())),
        )
        val exportedTagType = tagType(exceptionAttribute(), 2, exportedTagFunctionType)

        val byteStream = Resource(FILE_DIR + "module_info.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)
        val config = moduleConfig(decodeNameSection = true)

        val actual = module(reader, config)
            .map { module ->
                moduleInfo(module)
            }.getOrNull()

        val expected = ModuleInfo(
            imports = listOf(
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_function",
                    type = functionExternalType(importedFunctionType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_global",
                    type = globalExternalType(importedGlobalType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_memory",
                    type = memoryExternalType(importedMemoryType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_table",
                    type = tableExternalType(importedTableType),
                ),
                importDefinition(
                    moduleName = "env",
                    entityName = "imported_tag",
                    type = tagExternalType(importedTagType),
                ),
            ),
            exports = listOf(
                exportDefinition(
                    name = "exported_function",
                    type = functionExternalType(exportedFunctionType),
                    nameData = functionNameData,
                ),
                exportDefinition(
                    name = "exported_global",
                    type = globalExternalType(exportedGlobalType),
                ),
                exportDefinition(
                    name = "exported_memory",
                    type = memoryExternalType(exportedMemoryType),
                ),
                exportDefinition(
                    name = "exported_table",
                    type = tableExternalType(exportedTableType),
                ),
                exportDefinition(
                    name = "exported_tag",
                    type = tagExternalType(exportedTagType),
                ),
            ),
        )

        assertEquals(expected, actual)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
