package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StoragePlansTest {

    private val ART_SIZES = StructSizes(ArtifactTarget.ART)

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

        assertEquals("Struct_I1_R2__I_RR 24 open i0 r0", layout(plans.struct(shape)))
        assertEquals(layout(plans.struct(shape)), layout(plans.struct(other)))
        assertNotEquals(plans.struct(shape), plans.struct(other))
    }

    @Test
    fun `storage plans match the ABI goldens`() {

        val types = module("storage_plans").definedTypes

        assertEquals(resource("goldens/storage_plans.txt").decodeToString(), goldenLines(types, keys = null))
    }

    @Test
    fun `storage layout cases match their golden`() {

        val types = module("storage_layout_cases").definedTypes

        assertEquals(resource("goldens/storage_layout_cases.txt").decodeToString(), goldenLines(types, RttKeys(types)))
    }

    @Test
    fun `an empty struct uses the int root`() {

        val types = Types()
        val empty = types.struct()
        val plans = types.plans()

        assertEquals("Struct_I1_R0__I 16 open", layout(plans.struct(empty)))
    }

    @Test
    fun `a subtype of an empty struct can fill i0`() {

        val types = Types()
        val empty = types.struct()
        val child = types.subtype(empty, I32)
        val plans = types.plans()

        assertEquals("Struct_I1_R0__I 16 open i0", layout(plans.struct(child)))
    }

    @Test
    fun `a subtype of an empty struct stays below the int root`() {

        val types = Types()
        val empty = types.struct()
        val child = types.subtype(empty, ANYREF, ANYREF, ANYREF)
        val plans = types.plans()

        assertEquals("Struct_I1_R4__I_RR_RR 32 open r0 r1 r2", layout(plans.struct(child)))
    }

    @Test
    fun `a type shaped like the Kotlin header uses the header root`() {

        val types = Types()
        val header = types.struct(I32, ANYREF, ANYREF, ANYREF)
        val plans = types.plans()

        assertEquals("Struct_I1_R3__IRRR 32 open i0 r0 r1 r2", layout(plans.struct(header)))
    }

    @Test
    fun `a subtype of the header picks a single slot step for one added field`() {

        val types = Types()
        val header = types.struct(I32, ANYREF, ANYREF, ANYREF)
        val oneReference = types.subtype(header, ANYREF)
        val oneInt = types.subtype(header, I32)
        val plans = types.plans()

        assertEquals("Struct_I1_R4__IRRR_R 32 open i0 r0 r1 r2 r3", layout(plans.struct(oneReference)))
        assertEquals("Struct_I2_R3__IRRR_I 32 open i0 r0 r1 r2 i1", layout(plans.struct(oneInt)))
    }

    @Test
    fun `a subtype of the header picks a pair step for two added fields`() {

        val types = Types()
        val header = types.struct(I32, ANYREF, ANYREF, ANYREF)
        val twoReferences = types.subtype(header, ANYREF, ANYREF)
        val plans = types.plans()

        assertEquals("Struct_I1_R5__IRRR_RR 40 open i0 r0 r1 r2 r3 r4", layout(plans.struct(twoReferences)))
    }

    @Test
    fun `equal capacity prefers fewer superclasses`() {

        val types = Types()
        val struct = types.struct(I32, ANYREF, ANYREF, ANYREF, ANYREF)
        val plans = types.plans()

        assertEquals("Struct_I1_R4__IRRR_R 32 open i0 r0 r1 r2 r3", layout(plans.struct(struct)))
    }

    @Test
    fun `fewest slots wins over search order`() {

        val types = Types()
        val struct = types.struct(I32, ANYREF, ANYREF, ANYREF)
        val plans = types.plans()

        val storageClass = STORAGE_FAMILY.storageClass(plans.struct(struct).className)

        assertEquals(4, storageClass.intLanes + storageClass.referenceLanes)
    }

    @Test
    fun `two declarations of the same subtype chain get the same classes and slots`() {

        val first = Types()
        val header = first.struct(I32, ANYREF, ANYREF, ANYREF)
        val child = first.subtype(header, ANYREF, I32)
        val grandchild = first.subtype(child, I64, ANYREF)
        val firstPlans = first.plans()

        val second = Types()
        second.struct(F64, ANYREF)
        val secondHeader = second.struct(I32, ANYREF, ANYREF, ANYREF)
        second.struct(I32)
        val secondChild = second.subtype(secondHeader, ANYREF, I32)
        val secondGrandchild = second.subtype(secondChild, I64, ANYREF)
        val secondPlans = second.plans()

        assertEquals("Struct_I4_R6__IRRR_IR_IR_IRx1 56 open i0 r0 r1 r2 r3 i1 i2,i3 r4", layout(firstPlans.struct(grandchild)))
        assertEquals(layout(firstPlans.struct(header)), layout(secondPlans.struct(secondHeader)))
        assertEquals(layout(firstPlans.struct(child)), layout(secondPlans.struct(secondChild)))
        assertEquals(layout(firstPlans.struct(grandchild)), layout(secondPlans.struct(secondGrandchild)))
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
        val header = types.struct(I32, ANYREF, ANYREF, ANYREF)
        val mostInlineComponents = types.subtype(header, *Array(16) { ANYREF }, *Array(19) { I32 }, I64)
        val fewestUnusedInts = types.struct(I32, ANYREF)
        val smallestPath = types.struct(I32, ANYREF, I32)
        val plans = types.plans()

        assertEquals(DEEPEST_INT_SPILL + " 200 final " + ints(0, 21) + " i22,xi0", layout(plans.struct(fewestBytes)))
        assertEquals(
            "Struct_I17_R23__IRRR_RR_RR_IRx16_Spill 208 final " + references(0, 22) + " xr0 xr1",
            layout(plans.struct(mostInlineFields)),
        )
        assertEquals(
            "Struct_I21_R19__IRRR_II_II_IRx16_Spill 200 final i0 " + references(0, 18) + " " + ints(1, 19) + " i20,xi0",
            layout(plans.struct(mostInlineComponents)),
        )
        assertEquals("Struct_I1_R2__I_RR 24 open i0 r0", layout(plans.struct(fewestUnusedInts)))
        assertEquals("Struct_I2_R1__I_IR 24 open i0 r0 i1", layout(plans.struct(smallestPath)))
    }

    @Test
    fun `the first lane may differ from the first field`() {

        val types = Types()
        val struct = types.struct(ANYREF, I32, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I2_R1__I_IR 24 open r0 i0 i1", layout(storage))
        assertEquals("Struct_I2_R1__I_IR", declaringClass(storage, 0, 0))
        assertEquals("Struct_I1_R0__I", declaringClass(storage, 1, 0))
    }

    @Test
    fun `a single leftover lane prefers a reference spare`() {

        val types = Types()
        val ints = types.struct(I32, I32)
        val wide = types.struct(I64)
        val mixed = types.struct(I32, ANYREF)
        val plans = types.plans()

        assertEquals("Struct_I2_R1__I_IR 24 open i0 i1", layout(plans.struct(ints)))
        assertEquals("Struct_I2_R1__I_IR 24 open i0,i1", layout(plans.struct(wide)))
        assertEquals("Struct_I1_R2__I_RR 24 open i0 r0", layout(plans.struct(mixed)))
    }

    @Test
    fun `a type fills an earlier spare with a later field`() {

        val types = Types()
        val struct = types.struct(I32, ANYREF, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I2_R1__I_IR 24 open i0 r0 i1", layout(storage))
        assertEquals("Struct_I2_R1__I_IR", declaringClass(storage, 2, 0))
    }

    @Test
    fun `a grandchild fills a spare from an older level`() {

        val types = Types()
        val parent = types.struct(I32, ANYREF)
        val child = types.subtype(parent, I64)
        val grandchild = types.subtype(child, ANYREF)
        val plans = types.plans()

        val storage = plans.struct(grandchild)

        assertEquals("Struct_I1_R2__I_RR 24 open i0 r0", layout(plans.struct(parent)))
        assertEquals("Struct_I3_R2__I_RR_II 32 open i0 r0 i1,i2", layout(plans.struct(child)))
        assertEquals("Struct_I3_R2__I_RR_II 32 open i0 r0 i1,i2 r1", layout(storage))
        assertEquals("Struct_I1_R2__I_RR", declaringClass(storage, 3, 0))
    }

    @Test
    fun `the lowest matching free lane wins`() {

        val types = Types()
        val empty = types.struct()
        val child = types.subtype(empty, ANYREF, I32, I32)
        val plans = types.plans()

        val storage = plans.struct(child)

        assertEquals("Struct_I2_R1__I_IR 24 open r0 i0 i1", layout(storage))
        assertEquals("Struct_I1_R0__I", declaringClass(storage, 1, 0))
        assertEquals("Struct_I2_R1__I_IR", declaringClass(storage, 2, 0))
    }

    @Test
    fun `a wide field crosses pair levels`() {

        val types = Types()
        val struct = types.struct(I32, I32, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I4_R1__I_II_IR 32 open i0 i1 i2,i3", layout(storage))
        assertEquals("Struct_I3_R0__I_II", declaringClass(storage, 2, 0))
        assertEquals("Struct_I4_R1__I_II_IR", declaringClass(storage, 2, 1))
    }

    @Test
    fun `a wide field crosses the tree and the tail`() {

        val types = Types()
        val struct = types.struct(*Array(4) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I6_R1__I_II_II_IRx1 40 open " + ints(0, 3) + " i4,i5", layout(storage))
        assertEquals("Struct_I5_R0__I_II_II", declaringClass(storage, 4, 0))
        assertEquals("Struct_I6_R1__I_II_II_IRx1", declaringClass(storage, 4, 1))
    }

    @Test
    fun `a wide field crosses tail levels`() {

        val types = Types()
        val struct = types.struct(*Array(5) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I7_R2__I_II_II_IRx2 48 open " + ints(0, 4) + " i5,i6", layout(storage))
        assertEquals("Struct_I6_R1__I_II_II_IRx1", declaringClass(storage, 5, 0))
        assertEquals("Struct_I7_R2__I_II_II_IRx2", declaringClass(storage, 5, 1))
    }

    @Test
    fun `a wide field crosses the tail and spill`() {

        val types = Types()
        val struct = types.struct(*Array(22) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(DEEPEST_INT_SPILL + " 200 final " + ints(0, 21) + " i22,xi0", layout(storage))
        assertEquals(DEEPEST_INT_TAIL, declaringClass(storage, 22, 0))
        assertEquals(DEEPEST_INT_SPILL, declaringClass(storage, 22, 1))
    }

    @Test
    fun `a 64 bit field that straddles the last inline slot keeps its inline low half`() {

        val types = Types()
        val struct = types.struct(*Array(22) { I32 }, I64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(DEEPEST_INT_SPILL, simpleName(storage.className))
        assertTrue(layout(storage).endsWith(" i22,xi0"))
    }

    @Test
    fun `the low half of an inline 64 bit field has the lower slot`() {

        val types = Types()
        val structs = chains(types).flatten()
        val plans = types.plans()

        val inlineWideFields = structs.flatMap { struct -> plans.struct(struct).fields }
            .filter { field -> field.lanes.size == 2 && field.lanes.none { lane -> lane.spilled } }

        assertTrue(inlineWideFields.isNotEmpty())
        inlineWideFields.forEach { field -> assertTrue(field.lanes[0].index < field.lanes[1].index) }
    }

    @Test
    fun `spilling starts at the first overflowing component and never stops`() {

        val types = Types()
        val struct = types.struct(*Array(24) { I32 }, ANYREF)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals(DEEPEST_INT_SPILL + " 216 final " + ints(0, 22) + " xi0 xr0", layout(storage))
        assertEquals(TAIL_REFERENCE_LANES, STORAGE_FAMILY.storageClass(storage.className).referenceLanes)
    }

    @Test
    fun `a subtype of a spilled type keeps the terminal class`() {

        val types = Types()
        val spilled = types.struct(*Array(22) { I32 }, I64)
        val child = types.subtype(spilled, ANYREF, I32)
        val plans = types.plans()

        assertEquals(DEEPEST_INT_SPILL + " 224 final " + ints(0, 21) + " i22,xi0 xr0 xi1", layout(plans.struct(child)))
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
        assertEquals(TERMINAL_BYTES + 32 + 24, ART_SIZES.bytes(storage))
    }

    @Test
    fun `a packed field takes one lane and keeps its storage type`() {

        val types = Types()
        val struct = types.struct(I8, I16, I32)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I3_R0__I_II 24 open i0 i1 i2", layout(storage))
        assertEquals(listOf(I8, I16, I32), storage.fields.map { field -> field.type })
    }

    @Test
    fun `a float field keeps its raw bits`() {

        val types = Types()
        val struct = types.struct(F32, F64)
        val plans = types.plans()

        val storage = plans.struct(struct)

        assertEquals("Struct_I3_R0__I_II 24 open i0 i1,i2", layout(storage))
        assertEquals(listOf(F32, F64), storage.fields.map { field -> field.type })
    }

    @Test
    fun `default allocation includes the spill arrays it needs`() {

        val types = Types()
        val inline = types.struct(*Array(23) { I32 })
        val intSpill = types.struct(*Array(24) { I32 })
        val bothSpills = types.struct(*Array(24) { I32 }, ANYREF)
        val plans = types.plans()

        assertEquals(DEEPEST_INT_TAIL + " 176 open " + ints(0, 22), layout(plans.struct(inline)))
        assertEquals(DEEPEST_INT_SPILL + " 200 final " + ints(0, 22) + " xi0", layout(plans.struct(intSpill)))
        assertEquals(TERMINAL_BYTES + 16, ART_SIZES.bytes(plans.struct(intSpill)))
        assertEquals(TERMINAL_BYTES + 16 + 16, ART_SIZES.bytes(plans.struct(bothSpills)))
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
        assertEquals("Struct_I1_R0__I 16 open i0", layout(plans.struct(plain)))
    }

    @Test
    fun `only terminal storage classes are final`() {

        val types = Types()
        val tree = types.struct(I32, ANYREF)
        val spilled = types.struct(*Array(24) { I32 })
        val plans = types.plans()

        val classes = STORAGE_FAMILY.classes

        assertEquals(644, classes.size)
        assertEquals(47, classes.count { storageClass -> "x" !in simpleName(storageClass.name) && !storageClass.final })
        assertEquals(33, classes.count { storageClass -> storageClass.final })
        for (storageClass in classes) {
            assertEquals(storageClass.name.endsWith("_Spill;"), storageClass.final)
            assertEquals(storageClass.final, storageClass.subclasses.isEmpty())
        }
        assertFalse(plans.struct(tree).final)
        assertTrue(plans.struct(spilled).final)
    }

    @Test
    fun `every storage class name states its capacity and adds one step to its superclass`() {

        val classes = STORAGE_FAMILY.classes

        assertEquals(644, classes.map { storageClass -> storageClass.name }.toSet().size)
        classes.forEach { storageClass ->
            val name = simpleName(storageClass.name)
            val capacity = "Struct_I${storageClass.intLanes}_R${storageClass.referenceLanes}"

            assertEquals(capacity, name.substringBefore("__"), name)
            assertEquals(steps(storageClass.superclass) + letters(storageClass.lanes), steps(storageClass), name)
            assertEquals(storageClass.final, name.endsWith("_Spill"), name)
        }
    }

    @Test
    fun `storage class names read capacity first then path`() {
        assertEquals("Struct_I1_R0__I", simpleName(STORAGE_FAMILY.roots[0].name))
        assertEquals("Struct_I0_R1__R", simpleName(STORAGE_FAMILY.roots[1].name))
        assertEquals("Struct_I1_R3__IRRR", simpleName(STORAGE_FAMILY.roots[2].name))
        assertEquals("Struct_I1_R4__I_RR_RR", simpleName(STORAGE_FAMILY.roots[0].subclasses[2].subclasses[2].name))
        assertEquals("Struct_I1_R4__IRRR_R", simpleName(STORAGE_FAMILY.roots[2].subclasses[1].name))
        assertNotNull(storageClass("Struct_I4_R5__IRRR_I_IR_IRx1"))
        assertTrue(storageClass("Struct_I23_R18__I_II_II_IRx18_Spill").final)
    }

    @Test
    fun `the family has three branches of the expected size`() {

        val classes = STORAGE_FAMILY.classes

        assertEquals(47, classes.count { storageClass -> "x" !in simpleName(storageClass.name) })
        assertEquals(564, classes.count { storageClass -> "x" in simpleName(storageClass.name) && !storageClass.final })
        assertEquals(33, classes.count { storageClass -> storageClass.final })
        assertEquals(23, classes.maxOf { storageClass -> storageClass.intLanes })
        assertEquals(23, classes.maxOf { storageClass -> storageClass.referenceLanes })
    }

    private fun steps(storageClass: StorageClass?): List<String> {

        if (storageClass == null) {
            return emptyList()
        }

        return simpleName(storageClass.name).substringAfter("__").removeSuffix("_Spill").split("_").flatMap { segment ->
            List(segment.substringAfter("x", "1").toInt()) { segment.substringBefore("x") }
        }
    }

    private fun letters(lanes: List<Lane>): List<String> {
        return if (lanes.isEmpty()) emptyList() else listOf(lanes.joinToString("") { lane -> if (lane.kind == LaneKind.INT) "I" else "R" })
    }

    private fun storageClass(simpleName: String): StorageClass {
        return STORAGE_FAMILY.storageClass("L$STORAGE_PACKAGE$simpleName;")
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

    private fun goldenLines(
        types: List<DefinedType>,
        keys: RttKeys?,
    ): String {

        val plans = StoragePlans(types, STORAGE_FAMILY)
        val lines = types.indices.mapNotNull { index ->
            val label = if (keys == null) "$index" else "$index ${keys.key(types[index])}"
            try {
                when (types[index].recursiveType.subTypes[types[index].recursiveTypeIndex].compositeType) {
                    is CompositeType.Struct -> "$label ${layout(plans.struct(index))}"
                    is CompositeType.Array -> "$label ${simpleName(plans.array(index).className)} ${plans.array(index).elementShift}"
                    is CompositeType.Function -> if (keys == null) null else "$label function"
                }
            } catch (unsupported: UnsupportedShape) {
                "$index unsupported"
            }
        }

        return lines.joinToString("\n", postfix = "\n")
    }

    private fun layout(storage: StructStorage): String {

        val header = listOf(simpleName(storage.className), ART_SIZES.bytes(storage).toString(), if (storage.final) "final" else "open")
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
        const val DEEPEST_INT_TAIL = "Struct_I23_R18__I_II_II_IRx18"
        const val DEEPEST_INT_SPILL = DEEPEST_INT_TAIL + "_Spill"
        const val TAIL_REFERENCE_LANES = 18
        const val TERMINAL_BYTES = 184
        const val CHAINS = 200
        const val CHAIN_SEED = 7

        val I64 = FieldType(StorageType.Value(ValueType.Number(NumberType.I64)), Mutability.Var)
        val F32 = FieldType(StorageType.Value(ValueType.Number(NumberType.F32)), Mutability.Var)
        val F64 = FieldType(StorageType.Value(ValueType.Number(NumberType.F64)), Mutability.Var)
        val I8 = FieldType(StorageType.Packed(PackedType.I8), Mutability.Var)
        val I16 = FieldType(StorageType.Packed(PackedType.I16), Mutability.Var)
        val V128 = FieldType(StorageType.Value(ValueType.Vector(VectorType.V128)), Mutability.Var)
        val EQREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq))), Mutability.Var)
        val FUNCREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))), Mutability.Var)
        val CONST_ANYREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))), Mutability.Const)
        val CONST_EQREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Eq))), Mutability.Const)
    }
}

internal val I32 = FieldType(StorageType.Value(ValueType.Number(NumberType.I32)), Mutability.Var)

internal val ANYREF = FieldType(StorageType.Value(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Any))), Mutability.Var)
