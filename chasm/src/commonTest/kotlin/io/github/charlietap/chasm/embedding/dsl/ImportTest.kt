package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.constMutability
import io.github.charlietap.chasm.fixture.ast.type.exceptionAttribute
import io.github.charlietap.chasm.fixture.ast.type.functionHeapType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.limits
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.ast.type.tableType
import io.github.charlietap.chasm.fixture.ast.type.tagType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tagExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.functionReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
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
                    reference = functionExternalValue(
                        address = functionAddress(0),
                    ),
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
                    valueType = i32ValueType()
                    mutability = Mutability.Const
                }
                value = i32(0)
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
                    referenceType = refNullReferenceType(functionHeapType())
                    limits {
                        min = 1u
                    }
                }
                value = functionReferenceValue(functionAddress(0))
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

    @Test
    fun `can create a tag import using the import dsl builder`() {

        val store = publicStore()

        val actual = imports(store) {
            tag {
                moduleName = "err"
                entityName = "exception"
                type {
                    attribute = exceptionAttribute()
                    functionType {
                        params { i32() }
                        results { i32() }
                    }
                }
            }
        }

        val expectedTagType = tagType(
            attribute = exceptionAttribute(),
            type = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        )

        val expected = listOf<Import>(
            publicImport(
                moduleName = "err",
                entityName = "exception",
                value = publicTag(
                    reference = tagExternalValue(tagAddress(0)),
                ),
            ),
        )

        assertEquals(expected, actual)
        assertEquals(1, store.store.tags.size)
        assertEquals(expectedTagType, store.store.tags[0].type)
    }
}
