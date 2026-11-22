package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.exceptionAttribute
import io.github.charlietap.chasm.fixture.type.functionHeapType
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
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ImportTest {

    @Test
    fun `can create a function import using the import dsl builder`() {

        val runtime = store()
        val store = interpreterStore(runtime)

        val actual = imports(store) {
            function {
                moduleName = "wasi_preview_1"
                entityName = "fd_write"
                type {
                    params { i32() }
                    results { i32() }
                }
                reference(hostFunction { _, _ -> })
            }
        }

        val expectedType = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val import = actual.single()
        assertEquals("wasi_preview_1", import.moduleName)
        assertEquals("fd_write", import.entityName)
        assertEquals(expectedType, assertIs<Function>(import.value).type)
        assertEquals(1, runtime.functions.size)
    }

    @Test
    fun `can create a function import using an existing function type`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )

        val actual = imports(store) {
            function {
                moduleName = "wasi_preview_1"
                entityName = "fd_write"
                type(type)
                reference(hostFunction { _, _ -> })
            }
        }

        assertEquals(type, assertIs<Function>(actual.single().value).type)
        assertEquals(type, runtime.functions.single().functionType)
    }

    @Test
    fun `can create an exception reference function import`() {

        val runtime = store()
        val store = interpreterStore(runtime)

        imports(store) {
            function {
                moduleName = "errors"
                entityName = "round_trip"
                type {
                    params { exnref() }
                    results { exnref() }
                }
                reference(hostFunction { _, _ -> })
            }
        }

        val exnref = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Exception))
        val functionType = runtime.functions.single().functionType

        assertEquals(listOf(exnref), functionType.params.types)
        assertEquals(listOf(exnref), functionType.results.types)
    }

    @Test
    fun `can create a global import using the import dsl builder`() {

        val runtime = store()
        val store = interpreterStore(runtime)

        val actual = imports(store) {
            global {
                moduleName = "wasi_preview_1"
                entityName = "stack_pointer"
                type {
                    valueType = i32ValueType()
                    mutability = Mutability.Const
                }
                value = NumberValue.I32(0)
            }
        }

        val import = actual.single()
        assertEquals("wasi_preview_1", import.moduleName)
        assertEquals("stack_pointer", import.entityName)
        assertIs<Global>(import.value)
        assertEquals(1, runtime.globals.size)
        assertEquals(globalType(i32ValueType(), constMutability()), runtime.globals[0].type)
        assertEquals(0L, runtime.globals[0].value)
    }

    @Test
    fun `can create a memory import using the import dsl builder`() {

        val runtime = store()
        val store = interpreterStore(runtime)

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

        val import = actual.single()
        assertEquals("wasi_preview_1", import.moduleName)
        assertEquals("heap", import.entityName)
        assertIs<Memory>(import.value)
        assertEquals(1, runtime.memories.size)
        assertEquals(memoryType(i32AddressType(), limits(1u)), runtime.memories[0].type)
    }

    @Test
    fun `can create a table import using the import dsl builder`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val reference = functionReference(function(store, functionType(), HostFunction { _, _ -> }))

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
                value = reference
            }
        }

        val import = actual.single()
        assertEquals("wasi_preview_1", import.moduleName)
        assertEquals("dynamic_dispatch", import.entityName)
        val table = assertIs<Table>(import.value)
        assertEquals(1, runtime.tables.size)
        assertEquals(tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), limits(1u)), runtime.tables[0].type)
        assertEquals(reference, readTable(table, 0))
    }

    @Test
    fun `can create a tag import using the import dsl builder`() {

        val runtime = store()
        val store = interpreterStore(runtime)

        val actual = imports(store) {
            tag {
                moduleName = "err"
                entityName = "exception"
                type {
                    attribute = exceptionAttribute()
                    functionType {
                        params { i32() }
                    }
                }
            }
        }

        val expectedTagType = tagType(
            attribute = exceptionAttribute(),
            typeIndex = 0,
            functionType = functionType(params = resultType(listOf(i32ValueType()))),
        )

        val import = actual.single()
        assertEquals("err", import.moduleName)
        assertEquals("exception", import.entityName)
        assertEquals(expectedTagType, assertIs<Tag>(import.value).type)
        assertEquals(expectedTagType, runtime.heap.tag(Address.Tag(0)).type)
    }
}
