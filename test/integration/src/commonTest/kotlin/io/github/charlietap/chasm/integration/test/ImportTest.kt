package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.exceptionAttribute
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
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportTest {

    @Test
    fun `can run a wasm file with all types of import and return a correct result`() = integrationTest(FIXTURE) {

        val store = store()

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                ),
            ),
        )
        val hostFunction = HostFunction { _, _ -> }
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
        val tagType = tagType(exceptionAttribute(), 0, tagFunctionType)
        val tagExternal = tag(store, tagType)
        val tagImport = Import(
            "env",
            "tag",
            tagExternal,
        )

        val imports = listOf(
            functionImport,
            globalImport,
            memoryImport,
            tableImport,
            tagImport,
        )

        val result = runner(store, imports).invoke(
            name = "import",
        )

        val expected = emptyList<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result, engine.name())
    }

    private companion object {
        const val FIXTURE = "import"
    }
}
