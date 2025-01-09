package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.constMutability
import io.github.charlietap.chasm.fixture.ast.type.exceptionAttribute
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.limits
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.ast.type.tableType
import io.github.charlietap.chasm.fixture.ast.type.tagType
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportTest {

    @Test
    fun `can run a wasm file with all types of import and return a correct result`() {

        val store = Store(store())

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                ),
            ),
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

        val globalType = globalType(i32ValueType(), constMutability())
        val globalExternal = global(store, globalType, NumberValue.I32(117))
        val globalImport = Import(
            "env",
            "global",
            globalExternal,
        )

        val memoryType = memoryType(limits(1u))
        val memoryExternal = memory(store, memoryType)

        val memoryImport = Import(
            "env",
            "memory",
            memoryExternal,
        )

        val tableType = tableType(refNullReferenceType(AbstractHeapType.Func), limits(1u))
        val tableExternal = table(store, tableType, ReferenceValue.Null(AbstractHeapType.Func))
        val tableImport = Import(
            "env",
            "table",
            tableExternal,
        )

        val tagFunctionType = functionType(
            params = resultType(listOf(i32ValueType())),
        )
        val tagType = tagType(exceptionAttribute(), tagFunctionType)
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

        val expected = emptyList<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
