package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostExceptions
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import kotlin.test.Test
import kotlin.test.assertEquals

class CodegenImportTest {

    @Test
    fun `allocates function imports in declaration order`() {
        val store = publicStore()
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

        assertEquals(listOf("first-module", "second-module"), allocated.map { it.moduleName })
        assertEquals(listOf("first-function", "second-function"), allocated.map { it.entityName })
        assertEquals(listOf(firstType, secondType), store.store.functions.map { it.functionType })

        val stack = LongArray(1)
        val module = object : HostModuleInstance {}
        context(stack, module, UnusedHostResources) {
            allocated.forEach { import ->
                val function = import.value as Function
                val instance = store.store.function(function.reference.address)
                (instance as FunctionInstance.HostFunction).function.invoke(0, 0)
            }
        }
        assertEquals(listOf("first", "second"), invocations)
    }
}

private object UnusedHostResources : HostResources {
    override val references: HostReferences
        get() = error("unused")
    override val gc: HostGc
        get() = error("unused")
    override val externs: HostExterns
        get() = error("unused")
    override val exceptions: HostExceptions
        get() = error("unused")

    override fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory = error("unused")

    override fun growMemory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex, pagesToAdd: Int): Int =
        error("unused")

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable = error("unused")

    override fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int = error("unused")

    override fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal = error("unused")

    override fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag = error("unused")
}
