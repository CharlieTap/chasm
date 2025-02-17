// package io.github.charlietap.chasm.integration
//
// import com.goncalossilva.resources.Resource
// import io.github.charlietap.chasm.decoder.FakeSourceReader
// import io.github.charlietap.chasm.embedding.module
// import io.github.charlietap.chasm.embedding.moduleInfo
// import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
// import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
// import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
// import io.github.charlietap.chasm.embedding.shapes.getOrNull
// import io.github.charlietap.chasm.embedding.shapes.map
// import io.github.charlietap.chasm.fixture.executor.runtime.type.functionExternalType
// import io.github.charlietap.chasm.fixture.executor.runtime.type.globalExternalType
// import io.github.charlietap.chasm.fixture.executor.runtime.type.memoryExternalType
// import io.github.charlietap.chasm.fixture.executor.runtime.type.tableExternalType
// import io.github.charlietap.chasm.fixture.executor.runtime.type.tagExternalType
// import io.github.charlietap.chasm.fixture.ir.type.constMutability
// import io.github.charlietap.chasm.fixture.ir.type.exceptionAttribute
// import io.github.charlietap.chasm.fixture.ir.type.functionType
// import io.github.charlietap.chasm.fixture.ir.type.globalType
// import io.github.charlietap.chasm.fixture.ir.type.i32ValueType
// import io.github.charlietap.chasm.fixture.ir.type.i64ValueType
// import io.github.charlietap.chasm.fixture.ir.type.limits
// import io.github.charlietap.chasm.fixture.ir.type.memoryType
// import io.github.charlietap.chasm.fixture.ir.type.refNullReferenceType
// import io.github.charlietap.chasm.fixture.ir.type.resultType
// import io.github.charlietap.chasm.fixture.ir.type.tableType
// import io.github.charlietap.chasm.fixture.ir.type.tagType
// import io.github.charlietap.chasm.fixture.ir.type.varMutability
// import io.github.charlietap.chasm.ir.type.AbstractHeapType
// import kotlin.test.Test
// import kotlin.test.assertEquals
//
// class ModuleInfoTest {
//
//    @Test
//    fun `can return module info for all import and export types`() {
//
//        val importedFunctionType = functionType(
//            params = resultType(listOf(i32ValueType())),
//        )
//        val exportedFunctionType = functionType()
//
//        val importedGlobalType = globalType(i32ValueType(), constMutability())
//        val exportedGlobalType = globalType(i32ValueType(), varMutability())
//
//        val importedMemoryType = memoryType(limits(1u))
//        val exportedMemoryType = memoryType(limits(2u))
//
//        val importedTableType = tableType(refNullReferenceType(AbstractHeapType.Func), limits(1u))
//        val exportedTableType = tableType(refNullReferenceType(AbstractHeapType.Func), limits(2u))
//
//        val importedTagFunctionType = functionType(
//            params = resultType(listOf(i32ValueType())),
//        )
//        val importedTagType = tagType(exceptionAttribute(), importedTagFunctionType)
//        val exportedTagFunctionType = functionType(
//            params = resultType(listOf(i64ValueType())),
//        )
//        val exportedTagType = tagType(exceptionAttribute(), exportedTagFunctionType)
//
//        val byteStream = Resource(FILE_DIR + "module_info.wasm").readBytes()
//        val reader = FakeSourceReader(byteStream)
//
//        val actual = module(reader)
//            .map { module ->
//                moduleInfo(module)
//            }.getOrNull()
//
//        val expected = ModuleInfo(
//            imports = listOf(
//                ImportDefinition(
//                    moduleName = "env",
//                    entityName = "imported_function",
//                    type = functionExternalType(importedFunctionType),
//                ),
//                ImportDefinition(
//                    moduleName = "env",
//                    entityName = "imported_global",
//                    type = globalExternalType(importedGlobalType),
//                ),
//                ImportDefinition(
//                    moduleName = "env",
//                    entityName = "imported_memory",
//                    type = memoryExternalType(importedMemoryType),
//                ),
//                ImportDefinition(
//                    moduleName = "env",
//                    entityName = "imported_table",
//                    type = tableExternalType(importedTableType),
//                ),
//                ImportDefinition(
//                    moduleName = "env",
//                    entityName = "imported_tag",
//                    type = tagExternalType(importedTagType),
//                ),
//            ),
//            exports = listOf(
//                ExportDefinition(
//                    name = "exported_function",
//                    type = functionExternalType(exportedFunctionType),
//                ),
//                ExportDefinition(
//                    name = "exported_global",
//                    type = globalExternalType(exportedGlobalType),
//                ),
//                ExportDefinition(
//                    name = "exported_memory",
//                    type = memoryExternalType(exportedMemoryType),
//                ),
//                ExportDefinition(
//                    name = "exported_table",
//                    type = tableExternalType(exportedTableType),
//                ),
//                ExportDefinition(
//                    name = "exported_tag",
//                    type = tagExternalType(exportedTagType),
//                ),
//            ),
//        )
//
//        assertEquals(expected, actual)
//    }
//
//    companion object {
//        private const val FILE_DIR = "src/commonTest/resources/integration/"
//    }
// }
