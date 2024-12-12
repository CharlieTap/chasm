package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.fixture.publicMemoryType
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportTest {

    @Test
    fun `can run a wasm file with all types of import and return a correct result`() {

        val store = Store(store())

        val functionType = FunctionType(
            listOf(ValueType.Number.I32),
            emptyList(),
        )
        val hostFunction: HostFunction = {
            emptyList()
        }
        val functionExternal = function(store, functionType, hostFunction)
        val functionImport = Import(
            "env",
            "function",
            functionExternal,
        )

        val globalType = GlobalType(ValueType.Number.I32, Mutability.Const)
        val globalExternal = global(store, globalType, Value.Number.I32(117))
        val globalImport = Import(
            "env",
            "global",
            globalExternal,
        )

        val memoryType = publicMemoryType(Limits(1u))
        val memoryExternal = memory(store, memoryType)

        val memoryImport = Import(
            "env",
            "memory",
            memoryExternal,
        )

        val tableType = TableType(Limits(1u), ValueType.Reference.RefNull(HeapType.Func))
        val tableExternal = table(store, tableType, Value.Reference.Null(HeapType.Func))
        val tableImport = Import(
            "env",
            "table",
            tableExternal,
        )

        val tagFunctionType = FunctionType(listOf(ValueType.Number.I32), emptyList())
        val tagType = TagType(TagType.Attribute.Exception, tagFunctionType)
        val tagExternal = tag(store, tagType)
        val tagImport = Import(
            "env",
            "tag",
            tagExternal,
        )

        val result = testRunner(
            fileName = "import.wasm",
            fileDirectory = FILE_DIR,
            store = store,
            imports = listOf(
                functionImport,
                globalImport,
                memoryImport,
                tableImport,
                tagImport,
            ),
        )

        val expected = emptyList<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
