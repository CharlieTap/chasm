package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.fixture.ORDERED_IMPORTS_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CodegenImportTest {

    @Test
    fun `allocates function imports in declaration order`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val firstType = functionType()
        val secondType = functionType(params = resultType(listOf(i32ValueType())))
        val invocations = mutableListOf<String>()
        val imports = listOf(
            FunctionImport(
                "first-module",
                "first-function",
                firstType,
                HostFunction { _, _ ->
                    invocations += "first"
                },
            ),
            FunctionImport(
                "second-module",
                "second-function",
                secondType,
                HostFunction { _, _ ->
                    invocations += "second"
                },
            ),
        )

        val allocated = allocateImports(store, imports)

        assertEquals(listOf("first-module", "second-module"), allocated.map { import -> import.moduleName })
        assertEquals(listOf("first-function", "second-function"), allocated.map { import -> import.entityName })
        assertEquals(listOf(firstType, secondType), runtime.functions.map { function -> function.functionType })

        val instance = instantiate(store, ORDERED_IMPORTS_MODULE, allocated)
        invoke(instance, "first").expect("first runs")
        invoke(instance, "second", listOf(NumberValue.I32(0))).expect("second runs")

        assertEquals(listOf("first", "second"), invocations)
    }
}
