package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.runtime.store as runtimeStore

class AllocatorTest {

    @Test
    fun `prepares allocator functions once`() {
        val allocAddress = functionAddress(0)
        val freeAddress = functionAddress(1)
        val module = moduleInstance(
            exports = mutableListOf(
                exportInstance(nameValue("alloc"), functionExternalValue(allocAddress)),
                exportInstance(nameValue("free"), functionExternalValue(freeAddress)),
            ),
        )
        val freedAddresses = mutableListOf<Int>()
        val internalStore = runtimeStore(
            functions = mutableListOf(
                hostFunctionInstance(
                    functionType = functionType(
                        params = resultType(listOf(i32ValueType())),
                        results = resultType(listOf(i32ValueType())),
                    ),
                ) { parameters, results ->
                    results.writeI32(0, parameters.readI32(0) + 100)
                },
                hostFunctionInstance(
                    functionType = functionType(
                        params = resultType(listOf(i32ValueType())),
                    ),
                ) { parameters, _ ->
                    freedAddresses += parameters.readI32(0)
                },
            ),
        )
        val store = publicStore(internalStore)
        val allocator = Wasm32Allocator(
            instance = publicInstance(moduleInstance = module, store = internalStore),
            store = store,
            allocFunction = "alloc",
            freeFunction = "free",
        )

        module.exports.clear()

        val address = allocator.alloc(17)
        allocator.free(address)

        assertEquals(117, address)
        assertEquals(listOf(117), freedAddresses)
    }
}
