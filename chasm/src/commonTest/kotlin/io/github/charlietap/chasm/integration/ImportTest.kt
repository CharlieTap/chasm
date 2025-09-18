package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.exceptionAttribute
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32AddressType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.RecursiveType
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
        val definedType = definedType(functionRecursiveType(functionType, RecursiveType.State.CLOSED))
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

        val memoryType = memoryType(i32AddressType(), limits(1u))
        val memoryExternal = memory(store, memoryType)

        val memoryImport = Import(
            "env",
            "memory",
            memoryExternal,
        )

        val tableType = tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), limits(1u))
        val tableExternal = table(store, tableType, ReferenceValue.Null(AbstractHeapType.Func))
        val tableImport = Import(
            "env",
            "table",
            tableExternal,
        )

        val tagFunctionType = functionType(
            params = resultType(listOf(i32ValueType())),
        )
        val tagType = tagType(exceptionAttribute(), definedType, tagFunctionType)
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
