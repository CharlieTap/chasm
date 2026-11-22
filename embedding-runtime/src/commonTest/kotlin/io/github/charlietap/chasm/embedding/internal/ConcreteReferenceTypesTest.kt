package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.reference.allocateStruct
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.copyTable
import io.github.charlietap.chasm.embedding.table.fillTable
import io.github.charlietap.chasm.embedding.table.growTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class ConcreteReferenceTypesTest {

    @Test
    fun `a concrete global rejects a struct of an unrelated defined type`() {

        val fixture = StructHierarchy()
        val global = concreteGlobal(fixture.store, fixture.definedTypes[POINT])
        val other = fixture.allocate(OTHER)

        assertFailsWith<IllegalArgumentException> {
            writeGlobalReference(global, other)
        }
        assertNull(readGlobalReference(global))
    }

    @Test
    fun `a concrete global accepts a struct of its defined type and of a subtype`() {

        val fixture = StructHierarchy()
        val global = concreteGlobal(fixture.store, fixture.definedTypes[POINT])
        val point = fixture.allocate(POINT)
        val labelled = fixture.allocate(LABELLED_POINT)

        writeGlobalReference(global, point)
        assertEquals(point, readGlobalReference(global))

        writeGlobalReference(global, labelled)
        assertEquals(labelled, readGlobalReference(global))
    }

    @Test
    fun `a concrete global rejects a function of a different signature`() {

        val store = interpreterStore()
        val unary = FunctionType(ResultType(listOf(ValueType.Number(NumberType.I32))), ResultType(emptyList()))
        val nullary = FunctionType(ResultType(emptyList()), ResultType(emptyList()))
        val global = concreteGlobal(store, unary.definedType())
        val accepted = functionReference(function(store, unary, HostFunction { _, _ -> }))
        val rejected = functionReference(function(store, nullary, HostFunction { _, _ -> }))

        writeGlobalReference(global, accepted)

        assertFailsWith<IllegalArgumentException> {
            writeGlobalReference(global, rejected)
        }
        assertEquals(accepted, readGlobalReference(global))
    }

    @Test
    fun `a concrete table rejects a struct of an unrelated defined type on every write path`() {

        val fixture = StructHierarchy()
        val table = concreteTable(fixture.store, fixture.definedTypes[POINT])
        val other = fixture.allocate(OTHER)

        assertFailsWith<IllegalArgumentException> { writeTable(table, 0, other) }
        assertFailsWith<IllegalArgumentException> { fillTable(table, 0, other, 2) }
        assertFailsWith<IllegalArgumentException> { growTable(table, 1, other) }
        assertNull(readTable(table, 0))
    }

    @Test
    fun `copying between tables of different types checks each element against the destination`() {

        val fixture = StructHierarchy()
        val anyref = ReferenceType.RefNull(AbstractHeapType.Any)
        val source = table(fixture.store, TableType(AddressType.I32, anyref, Limits(2u)), null)
        val destination = concreteTable(fixture.store, fixture.definedTypes[POINT])
        val point = fixture.allocate(POINT)
        writeTable(source, 0, point)
        writeTable(source, 1, I31Reference(7))

        copyTable(source, 0, destination, 0, 1)

        assertEquals(point, readTable(destination, 0))
        assertFailsWith<IllegalArgumentException> {
            copyTable(source, 1, destination, 1, 1)
        }
        assertNull(readTable(destination, 1))
    }

    private fun concreteGlobal(
        store: Store,
        definedType: DefinedType,
    ): Global {

        val type = ReferenceType.RefNull(ConcreteHeapType.Defined(definedType))

        return global(store, GlobalType(ValueType.Reference(type), Mutability.Var), ReferenceValue.Null(AbstractHeapType.None))
    }

    private fun concreteTable(
        store: Store,
        definedType: DefinedType,
    ): Table {

        val type = ReferenceType.RefNull(ConcreteHeapType.Defined(definedType))

        return table(store, TableType(AddressType.I32, type, Limits(2u)), null)
    }

    private class StructHierarchy(
        val runtime: RuntimeStore = store(),
    ) {

        val store = interpreterStore(runtime)

        val definedTypes = DefinedTypeFactory(
            listOf(
                group(SubType.Open(emptyList(), struct(NumberType.I32))),
                group(SubType.Final(listOf(ConcreteHeapType.TypeIndex(POINT)), struct(NumberType.I32, NumberType.I64))),
                group(SubType.Final(emptyList(), struct(NumberType.I64))),
            ),
        )

        private val runtimeTypes = runtime.heap.registerRuntimeTypes(definedTypes)

        fun allocate(typeIndex: Int): StructReference {

            val type = HostGcType(runtimeTypes[typeIndex].value)
            val fieldCount = (definedTypes[typeIndex].recursiveType.subTypes.single().compositeType as CompositeType.Struct).structType.fields.size

            return allocateStruct(store, type, LongArray(fieldCount), arrayOfNulls(fieldCount))
        }

        private fun group(subType: SubType): RecursiveType {
            return RecursiveType(listOf(subType), RecursiveType.State.SYNTAX)
        }

        private fun struct(vararg fields: NumberType): CompositeType {
            return CompositeType.Struct(
                StructType(fields.map { field -> FieldType(StorageType.Value(ValueType.Number(field)), Mutability.Var) }),
            )
        }
    }

    private companion object {
        const val POINT = 0
        const val LABELLED_POINT = 1
        const val OTHER = 2
    }
}
