package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class StoragePlansTest {

    @Test
    fun `a canonical type gets the same plan in every module`() {

        val types = module("rtt_keys").definedTypes
        val copyTypes = module("rtt_keys_copy").definedTypes
        val keys = RttKeys(types)
        val copyKeys = RttKeys(copyTypes)
        val plans = StoragePlans(types, STORAGE_FAMILY)
        val copyPlans = StoragePlans(copyTypes, STORAGE_FAMILY)

        for ((index, copyIndex) in listOf(LEAF to COPIED_LEAF, FIRST to COPIED_FIRST, SECOND to COPIED_SECOND)) {
            assertEquals(keys[index], copyKeys[copyIndex])
            assertEquals(layout(plans.struct(index)), layout(copyPlans.struct(copyIndex)))
        }
    }

    @Test
    fun `module indices and planning order do not change a plan`() {

        val ordered = Types()
        val parent = ordered.struct(I32, ANYREF)
        val child = ordered.subtype(parent, I64)
        val grandchild = ordered.subtype(child, ANYREF, F64)
        val orderedPlans = ordered.plans()

        val reordered = Types()
        reordered.struct(F32)
        val reorderedGrandchild = reordered.declare(3, I32, ANYREF, I64, ANYREF, F64)
        reordered.struct(ANYREF)
        val reorderedChild = reordered.declare(4, I32, ANYREF, I64)
        val reorderedParent = reordered.declare(null, I32, ANYREF)
        val reorderedPlans = reordered.plans()

        assertEquals(layout(orderedPlans.struct(parent)), layout(reorderedPlans.struct(reorderedParent)))
        assertEquals(layout(orderedPlans.struct(child)), layout(reorderedPlans.struct(reorderedChild)))
        assertEquals(layout(orderedPlans.struct(grandchild)), layout(reorderedPlans.struct(reorderedGrandchild)))
    }

    @Test
    fun `unrelated types may share a class`() {

        val types = Types()
        val shape = types.struct(I32, ANYREF)
        val other = types.struct(F32, FUNCREF)
        val plans = types.plans()

        assertEquals("S_I_RR 24 open i0 r0", layout(plans.struct(shape)))
        assertEquals(layout(plans.struct(shape)), layout(plans.struct(other)))
        assertNotEquals(plans.struct(shape), plans.struct(other))
    }

    @Test
    fun `storage plans match the ABI goldens`() {

        val types = module("storage_plans").definedTypes
        val plans = StoragePlans(types, STORAGE_FAMILY)

        val lines = types.indices.mapNotNull { index ->
            try {
                when (types[index].recursiveType.subTypes[types[index].recursiveTypeIndex].compositeType) {
                    is CompositeType.Struct -> "$index ${layout(plans.struct(index))}"
                    is CompositeType.Array -> "$index ${simpleName(plans.array(index).className)} ${plans.array(index).elementShift}"
                    is CompositeType.Function -> null
                }
            } catch (unsupported: UnsupportedShape) {
                "$index unsupported"
            }
        }

        assertEquals(resource("goldens/storage_plans.txt").decodeToString(), lines.joinToString("\n", postfix = "\n"))
    }

    @Test
    fun `an empty struct uses S_I`() {

        val types = Types()
        val empty = types.struct()
        val plans = types.plans()

        assertEquals("S_I 16 open", layout(plans.struct(empty)))
    }

    @Test
    fun `a subtype of an empty struct can fill i0`() {

        val types = Types()
        val empty = types.struct()
        val child = types.subtype(empty, I32)
        val plans = types.plans()

        assertEquals("S_I 16 open i0", layout(plans.struct(child)))
    }

    @Test
    fun `a subtype with no new fields keeps its parent's plan`() {

        val types = Types()
        val parent = types.struct(I32, CONST_ANYREF)
        val covariant = types.declare(parent, I32, CONST_EQREF)
        val spilled = types.struct(*Array(22) { I32 }, I64)
        val spilledChild = types.subtype(spilled)
        val plans = types.plans()

        assertEquals(layout(plans.struct(parent)), layout(plans.struct(covariant)))
        assertEquals(CONST_EQREF, plans.struct(covariant).fields[1].type)
        assertEquals(plans.struct(spilled), plans.struct(spilledChild))
    }

    @Test
    fun `every ancestor lane and declaring class is preserved`() {

        val types = Types()
        val chains = chains(types)
        val plans = types.plans()

        for (chain in chains) {
            for ((position, ancestor) in chain.withIndex()) {
                for (descendant in chain.subList(position + 1, chain.size)) {

                    val ancestorStorage = plans.struct(ancestor)
                    val descendantStorage = plans.struct(descendant)

                    ancestorStorage.fields.forEachIndexed { field, storage ->
                        assertEquals(storage.lanes, descendantStorage.fields[field].lanes)
                        storage.lanes.indices.forEach { component ->
                            assertEquals(declaringClass(ancestorStorage, field, component), declaringClass(descendantStorage, field, component))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `a subtype's class is its parent's class or a descendant`() {

        val types = Types()
        val chains = chains(types)
        val plans = types.plans()

        for (chain in chains) {
            chain.zipWithNext { parent, child ->
                assertTrue(descends(plans.struct(child).className, plans.struct(parent).className))
            }
        }
    }

    @Test
    fun `lane kinds match and addresses are unique`() {

        val types = Types()
        val structs = chains(types).flatten()
        val plans = types.plans()

        for (struct in structs) {

            val storage = plans.struct(struct)
            val storageClass = STORAGE_FAMILY.storageClass(storage.className)
            val addresses = storage.fields.flatMap { field -> field.lanes }

            for (field in storage.fields) {
                assertEquals(laneKinds(field.type), field.lanes.map { lane -> lane.kind })
            }
            assertEquals(addresses.size, addresses.toSet().size)
            for (lane in addresses) {
                if (lane.spilled) {
                    assertTrue(storageClass.final)
                } else {
                    assertTrue(lane.index < if (lane.kind == LaneKind.INT) storageClass.intLanes else storageClass.referenceLanes)
                }
            }
        }
    }

    @Test
    fun `the ranking applies every tie breaker`() {

        val types = Types()
        val fewestBytes = types.struct(*Array(22) { I32 }, I64)
        val mostInlineFields = types.struct(*Array(25) { ANYREF })
        val referenceRoot = types.struct(ANYREF)
        val mostInlineComponents = types.subtype(referenceRoot, *Array(21) { I32 }, I64, I32)
        val fewestUnusedInts = types.struct(I32, ANYREF)
        val smallestPath = types.struct(I32, ANYREF, I32)
        val plans = types.plans()

        assertEquals(TREE_LEAF + "_X 192 final " + ints(0, 21) + " i22,xi0", layout(plans.struct(fewestBytes)))
        assertEquals("S_R_RR_RR_RR_X 200 final " + references(0, 22) + " xr0 xr1", layout(plans.struct(mostInlineFields)))
        assertEquals("S_R_II_II_II_X 200 final r0 " + ints(0, 20) + " i21,xi0 xi1", layout(plans.struct(mostInlineComponents)))
        assertEquals("S_I_RR 24 open i0 r0", layout(plans.struct(fewestUnusedInts)))
        assertEquals("S_I_IR 24 open i0 r0 i1", layout(plans.struct(smallestPath)))
    }

    @Test
    fun `the first lane may differ from the first field`() {

        val types = Types()
        val struct = types.struct(ANYREF, I32, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("S_I_IR 24 open r0 i0 i1", layout(storage))
        assertEquals("S_I_IR", declaringClass(storage, 0, 0))
        assertEquals("S_I", declaringClass(storage, 1, 0))
    }

    @Test
    fun `a single leftover lane prefers a reference spare`() {

        val types = Types()
        val ints = types.struct(I32, I32)
        val wide = types.struct(I64)
        val mixed = types.struct(I32, ANYREF)
        val plans = types.plans()

        assertEquals("S_I_IR 24 open i0 i1", layout(plans.struct(ints)))
        assertEquals("S_I_IR 24 open i0,i1", layout(plans.struct(wide)))
        assertEquals("S_I_RR 24 open i0 r0", layout(plans.struct(mixed)))
    }

    @Test
    fun `a type fills an earlier spare with a later field`() {

        val types = Types()
        val struct = types.struct(I32, ANYREF, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("S_I_IR 24 open i0 r0 i1", layout(storage))
        assertEquals("S_I_IR", declaringClass(storage, 2, 0))
    }

    @Test
    fun `a grandchild fills a spare from an older level`() {

        val types = Types()
        val parent = types.struct(I32, ANYREF)
        val child = types.subtype(parent, I64)
        val grandchild = types.subtype(child, ANYREF)
        val plans = types.plans()

        val storage = plans.struct(grandchild)

        assertEquals("S_I_RR 24 open i0 r0", layout(plans.struct(parent)))
        assertEquals("S_I_RR_II 32 open i0 r0 i1,i2", layout(plans.struct(child)))
        assertEquals("S_I_RR_II 32 open i0 r0 i1,i2 r1", layout(storage))
        assertEquals("S_I_RR", declaringClass(storage, 3, 0))
    }

    @Test
    fun `the lowest matching free lane wins`() {

        val types = Types()
        val empty = types.struct()
        val child = types.subtype(empty, ANYREF, I32, I32)
        val plans = types.plans()

        val storage = plans.struct(child)

        assertEquals("S_I_IR 24 open r0 i0 i1", layout(storage))
        assertEquals("S_I", declaringClass(storage, 1, 0))
        assertEquals("S_I_IR", declaringClass(storage, 2, 0))
    }

    @Test
    fun `a wide field crosses pair levels`() {

        val types = Types()
        val struct = types.struct(I32, I32, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("S_I_II_IR 32 open i0 i1 i2,i3", layout(storage))
        assertEquals("S_I_II", declaringClass(storage, 2, 0))
        assertEquals("S_I_II_IR", declaringClass(storage, 2, 1))
    }

    @Test
    fun `a wide field crosses the tree and the tail`() {

        val types = Types()
        val struct = types.struct(*Array(6) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(TREE_LEAF + "_T1 48 open " + ints(0, 5) + " i6,i7", layout(storage))
        assertEquals(TREE_LEAF, declaringClass(storage, 6, 0))
        assertEquals(TREE_LEAF + "_T1", declaringClass(storage, 6, 1))
    }

    @Test
    fun `a wide field crosses tail levels`() {

        val types = Types()
        val struct = types.struct(*Array(7) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(TREE_LEAF + "_T2 56 open " + ints(0, 6) + " i7,i8", layout(storage))
        assertEquals(TREE_LEAF + "_T1", declaringClass(storage, 7, 0))
        assertEquals(TREE_LEAF + "_T2", declaringClass(storage, 7, 1))
    }

    @Test
    fun `a wide field crosses the tail and spill`() {

        val types = Types()
        val struct = types.struct(*Array(22) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(TREE_LEAF + "_X 192 final " + ints(0, 21) + " i22,xi0", layout(storage))
        assertEquals(TREE_LEAF + "_T16", declaringClass(storage, 22, 0))
        assertEquals(TREE_LEAF + "_X", declaringClass(storage, 22, 1))
    }

    @Test
    fun `spilling starts at the first overflowing component and never stops`() {

        val types = Types()
        val struct = types.struct(*Array(24) { I32 }, ANYREF)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(TREE_LEAF + "_X 208 final " + ints(0, 22) + " xi0 xr0", layout(storage))
        assertEquals(TAIL_REFERENCE_LANES, STORAGE_FAMILY.storageClass(storage.className).referenceLanes)
    }

    @Test
    fun `a subtype of a spilled type keeps the terminal class`() {

        val types = Types()
        val spilled = types.struct(*Array(22) { I32 }, I64)
        val child = types.subtype(spilled, ANYREF, I32)
        val plans = types.plans()

        assertEquals(TREE_LEAF + "_X 216 final " + ints(0, 21) + " i22,xi0 xr0 xi1", layout(plans.struct(child)))
    }

    @Test
    fun `spill arrays have independent dense indices and exact lengths`() {

        val types = Types()
        val spilled = types.struct(*Array(22) { I32 }, I64)
        val child = types.subtype(spilled, ANYREF, I32)
        val grandchild = types.subtype(child, ANYREF, I64)
        val plans = types.plans()

        val storage = plans.struct(grandchild)
        val spilledLanes = storage.fields.flatMap { field -> field.lanes }.filter { lane -> lane.spilled }

        assertEquals(listOf(0, 1, 2, 3), spilledLanes.filter { lane -> lane.kind == LaneKind.INT }.map { lane -> lane.index })
        assertEquals(listOf(0, 1), spilledLanes.filter { lane -> lane.kind == LaneKind.REFERENCE }.map { lane -> lane.index })
        assertEquals(TERMINAL_BYTES + 32 + 24, storage.bytes)
    }

    @Test
    fun `a packed field takes one lane and keeps its storage type`() {

        val types = Types()
        val struct = types.struct(I8, I16, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("S_I_II 24 open i0 i1 i2", layout(storage))
        assertEquals(listOf(I8, I16, I32), storage.fields.map { field -> field.type })
    }

    @Test
    fun `a float field keeps its raw bits`() {

        val types = Types()
        val struct = types.struct(F32, F64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("S_I_II 24 open i0 i1,i2", layout(storage))
        assertEquals(listOf(F32, F64), storage.fields.map { field -> field.type })
    }

    @Test
    fun `default allocation includes the spill arrays it needs`() {

        val types = Types()
        val inline = types.struct(*Array(23) { I32 })
        val intSpill = types.struct(*Array(24) { I32 })
        val bothSpills = types.struct(*Array(24) { I32 }, ANYREF)
        val plans = types.plans()

        assertEquals(TREE_LEAF + "_T16 168 open " + ints(0, 22), layout(plans.struct(inline)))
        assertEquals(TREE_LEAF + "_X 192 final " + ints(0, 22) + " xi0", layout(plans.struct(intSpill)))
        assertEquals(TERMINAL_BYTES + 16, plans.struct(intSpill).bytes)
        assertEquals(TERMINAL_BYTES + 16 + 16, plans.struct(bothSpills).bytes)
    }

    @Test
    fun `a v128 field is refused`() {

        val types = Types()
        val vector = types.struct(V128)
        val child = types.subtype(vector, I32)
        val vectors = types.array(V128)
        val plain = types.struct(I32)
        val plans = types.plans()

        assertFailsWith<UnsupportedShape> { plans.struct(vector) }
        assertFailsWith<UnsupportedShape> { plans.struct(child) }
        assertFailsWith<UnsupportedShape> { plans.array(vectors) }
        assertEquals("S_I 16 open i0", layout(plans.struct(plain)))
    }

    @Test
    fun `only terminal storage classes are final`() {

        val types = Types()
        val tree = types.struct(I32, ANYREF)
        val spilled = types.struct(*Array(24) { I32 })
        val plans = types.plans()

        val classes = STORAGE_FAMILY.classes

        assertEquals(998, classes.size)
        assertEquals(80, classes.count { storageClass -> "_T" !in storageClass.name && !storageClass.final })
        assertEquals(54, classes.count { storageClass -> storageClass.final })
        for (storageClass in classes) {
            assertEquals(storageClass.name.endsWith("_X;"), storageClass.final)
            assertEquals(storageClass.final, storageClass.subclasses.isEmpty())
        }
        assertFalse(plans.struct(tree).final)
        assertTrue(plans.struct(spilled).final)
    }

    private fun chains(types: Types): List<List<Int>> {

        val random = Random(CHAIN_SEED)
        val kinds = listOf(I32, I64, F32, F64, I8, I16, ANYREF, EQREF)

        return List(CHAINS) {
            val root = types.struct(*Array(random.nextInt(0, 12)) { kinds.random(random) })
            generateSequence(root) { parent ->
                types.subtype(parent, *Array(random.nextInt(0, 9)) { kinds.random(random) })
            }.take(random.nextInt(2, 8)).toList()
        }
    }

    private fun layout(storage: StructStorage): String {

        val header = listOf(simpleName(storage.className), storage.bytes.toString(), if (storage.final) "final" else "open")
        val fields = storage.fields.map { field -> field.lanes.joinToString(",", transform = ::address) }

        return (header + fields).joinToString(" ")
    }

    private fun address(lane: Lane): String {

        val spill = if (lane.spilled) "x" else ""
        val kind = if (lane.kind == LaneKind.INT) "i" else "r"

        return spill + kind + lane.index
    }

    private fun ints(
        first: Int,
        last: Int,
    ): String {
        return (first..last).joinToString(" ") { index -> "i$index" }
    }

    private fun references(
        first: Int,
        last: Int,
    ): String {
        return (first..last).joinToString(" ") { index -> "r$index" }
    }

    private fun declaringClass(
        storage: StructStorage,
        field: Int,
        component: Int,
    ): String {

        val storageClass = STORAGE_FAMILY.storageClass(storage.className)
        val lane = storage.fields[field].lanes[component]

        return simpleName(storageClass.declaringClass(lane).name)
    }

    private fun descends(
        className: String,
        ancestorName: String,
    ): Boolean {
        return generateSequence(STORAGE_FAMILY.storageClass(className)) { storageClass -> storageClass.superclass }
            .any { storageClass -> storageClass.name == ancestorName }
    }

    private fun laneKinds(type: FieldType): List<LaneKind> {
        return when (type) {
            I64, F64 -> listOf(LaneKind.INT, LaneKind.INT)
            ANYREF, EQREF -> listOf(LaneKind.REFERENCE)
            else -> listOf(LaneKind.INT)
        }
    }

    private fun simpleName(className: String): String {
        return className.removePrefix("L$STORAGE_PACKAGE").removeSuffix(";")
    }

    private fun module(name: String): Module {

        val bytes = resource("fixtures/$name.wasm")
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private fun resource(path: String): ByteArray {
        return checkNotNull(javaClass.classLoader.getResourceAsStream(path)) { "missing resource $path" }
            .use { stream -> stream.readBytes() }
    }

    private class Types {
        private val types = ArrayList<DefinedType>()

        fun struct(vararg fields: FieldType): Int {
            return declare(null, *fields)
        }

        fun subtype(
            parent: Int,
            vararg fields: FieldType,
        ): Int {

            val inherited = (types[parent].recursiveType.subTypes.single().compositeType as CompositeType.Struct).structType.fields

            return declare(parent, *inherited.toTypedArray(), *fields)
        }

        fun declare(
            parent: Int?,
            vararg fields: FieldType,
        ): Int {

            val superTypes = listOfNotNull(parent?.let(ConcreteHeapType::TypeIndex))
            val subType = SubType.Open(superTypes, CompositeType.Struct(StructType(fields.toList())))

            return add(subType)
        }

        fun array(element: FieldType): Int {
            return add(SubType.Final(emptyList(), CompositeType.Array(ArrayType(element))))
        }

        fun plans(): StoragePlans {
            return StoragePlans(types, STORAGE_FAMILY)
        }

        private fun add(subType: SubType): Int {

            val index = types.size
            types.add(DefinedType(RecursiveType(listOf(subType), RecursiveType.State.SYNTAX), 0, index))

            return index
        }
    }

    private companion object {
        const val LEAF = 0
        const val FIRST = 1
        const val SECOND = 2
        const val COPIED_LEAF = 1
        const val COPIED_FIRST = 2
        const val COPIED_SECOND = 3
        const val TREE_LEAF = "S_I_II_II_II"
        const val TAIL_REFERENCE_LANES = 16
        const val TERMINAL_BYTES = 176
        const val CHAINS = 200
        const val CHAIN_SEED = 7

        val I32 = FieldType(StorageType.Value(ValueType.Number(NumberType.I32)), Mutability.Var)
        val I64 = FieldType(StorageType.Value(ValueType.Number(NumberType.I64)), Mutability.Var)
        val F32 = FieldType(StorageType.Value(ValueType.Number(NumberType.F32)), Mutability.Var)
        val F64 = FieldType(StorageType.Value(ValueType.Number(NumberType.F64)), Mutability.Var)
        val I8 = FieldType(StorageType.Packed(PackedType.I8), Mutability.Var)
        val I16 = FieldType(StorageType.Packed(PackedType.I16), Mutability.Var)
        val V128 = FieldType(StorageType.Value(ValueType.Vector(VectorType.V128)), Mutability.Var)
        val ANYREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))), Mutability.Var)
        val EQREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq))), Mutability.Var)
        val FUNCREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))), Mutability.Var)
        val CONST_ANYREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))), Mutability.Const)
        val CONST_EQREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq))), Mutability.Const)
    }
}
