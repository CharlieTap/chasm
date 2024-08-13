package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportTest {

    @Test
    fun `can run a wasm file with a host_function and return a correct result`() {

        val store = Store(store())

        val memoryType = MemoryType(Limits(1u))
        val memoryExternal = memory(store, memoryType)

        val memoryImport = Import(
            "env",
            "memory",
            memoryExternal,
        )

        val functionType = FunctionType(
            listOf(ValueType.Number.I32),
            emptyList(),
        )

        val hostFunction = object : HostFunction {
            override fun invoke(p1: List<Value>): List<Value> {
                println("hello")
                return emptyList()
            }
        }

        val external = function(store, functionType, hostFunction)
        val printlnImport = Import(
            "env",
            "println",
            external,
        )

        val result = testRunner(
            fileName = "import.wasm",
            fileDirectory = FILE_DIR,
            store = store,
            imports = listOf(memoryImport, printlnImport),
        )

        val expected = emptyList<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/embedding/"
    }
}
