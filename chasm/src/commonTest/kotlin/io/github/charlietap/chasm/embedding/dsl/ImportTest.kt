package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.instance.memoryAddress
import io.github.charlietap.chasm.fixture.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.tableType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportTest {

    @Test
    fun `can create a function import using the import dsl builder`() {

        val store = publicStore()

        val hostFunction = hostFunction {
            emptyList()
        }

        val actual = imports(store) {
            function {
                moduleName = "wasi_preview_1"
                entityName = "fd_write"
                type {
                    params { i32() }
                    results { i32() }
                }
                reference(hostFunction)
            }
        }

        val expected = listOf<Import>(
            publicImport(
                moduleName = "wasi_preview_1",
                entityName = "fd_write",
                value = publicFunction(
                    reference = functionExternalValue(functionAddress(0)),
                ),
            ),
        )

        assertEquals(expected, actual)
        assertEquals(1, store.store.functions.size)
    }

    @Test
    fun `can create a global import using the import dsl builder`() {

        val store = publicStore()

        val actual = imports(store) {
            global {
                moduleName = "wasi_preview_1"
                entityName = "stack_pointer"
                type {
                    valueType = ValueType.Number.I32
                    mutability = Mutability.Const
                }
                value = Value.Number.I32(0)
            }
        }

        val expected = listOf<Import>(
            publicImport(
                moduleName = "wasi_preview_1",
                entityName = "stack_pointer",
                value = publicGlobal(
                    reference = globalExternalValue(globalAddress(0)),
                ),
            ),
        )

        assertEquals(expected, actual)
        assertEquals(1, store.store.globals.size)
        assertEquals(globalType(i32ValueType(), constMutability()), store.store.globals[0].type)
        assertEquals(0, (store.store.globals[0].value as NumberValue.I32).value)
    }

    @Test
    fun `can create a memory import using the import dsl builder`() {

        val store = publicStore()

        val actual = imports(store) {
            memory {
                moduleName = "wasi_preview_1"
                entityName = "heap"
                type {
                    limits {
                        min = 1u
                    }
                }
            }
        }

        val expected = listOf<Import>(
            publicImport(
                moduleName = "wasi_preview_1",
                entityName = "heap",
                value = publicMemory(
                    reference = memoryExternalValue(memoryAddress(0)),
                ),
            ),
        )

        assertEquals(expected, actual)
        assertEquals(1, store.store.memories.size)
        assertEquals(memoryType(limits(1u)), store.store.memories[0].type)
    }

    @Test
    fun `can create a table import using the import dsl builder`() {

        val store = publicStore()

        val actual = imports(store) {
            table {
                moduleName = "wasi_preview_1"
                entityName = "dynamic_dispatch"
                type {
                    referenceType = ValueType.Reference.RefNull(HeapType.Func)
                    limits {
                        min = 1u
                    }
                }
                value = Value.Reference.Func(0)
            }
        }

        val expected = listOf<Import>(
            publicImport(
                moduleName = "wasi_preview_1",
                entityName = "dynamic_dispatch",
                value = publicTable(
                    reference = tableExternalValue(tableAddress(0)),
                ),
            ),
        )

        assertEquals(expected, actual)
        assertEquals(1, store.store.tables.size)
        assertEquals(tableType(refNullReferenceType(AbstractHeapType.Func), limits(1u)), store.store.tables[0].type)
        assertEquals(functionAddress(0), (store.store.tables[0].elements[0] as ReferenceValue.Function).address)
    }
}
