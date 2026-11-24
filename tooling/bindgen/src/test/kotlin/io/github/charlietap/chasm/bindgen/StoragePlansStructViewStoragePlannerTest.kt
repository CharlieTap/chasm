package io.github.charlietap.chasm.bindgen

import com.github.michaelbull.result.getOrThrow
import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttKeys
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.subType
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import kotlin.test.Test
import kotlin.test.assertEquals

class StoragePlansStructViewStoragePlannerTest {

    private val types = definedTypes("storage_planner")
    private val keys = RttKeys(types)
    private val plans = StoragePlans(types, STORAGE_FAMILY)
    private val planner = StoragePlansStructViewStoragePlanner(RttKeys(emptyList()))

    @Test
    fun `a view plans the storage class, lanes and RTT key the module plans`() {

        val structs = types.indices.filter { index -> types[index].subType().compositeType is CompositeType.Struct }

        structs.forEach { index ->

            val plan = plans.struct(index)
            val descriptor = plan.className.removePrefix("L").removeSuffix(";").replace('/', '.')

            val storage = planner.storage(types[index])

            assertEquals(ClassName(descriptor.substringBeforeLast('.'), descriptor.substringAfterLast('.')), storage.storageClass)
            assertEquals(keys[index], storage.check.key)
            assertEquals(plan.fields.map { field -> field.lanes }, storage.fieldLanes.map(::lanes))
        }
    }

    @Test
    fun `a view checks its type's depth and finality`() {

        val checks = listOf(BASE, MIDDLE, LEAF, HOLDER).map { index -> planner.storage(types[index]).check }

        assertEquals(
            listOf(
                RttCheck(keys[BASE], 0, false),
                RttCheck(keys[MIDDLE], 1, false),
                RttCheck(keys[LEAF], 2, true),
                RttCheck(keys[HOLDER], 0, true),
            ),
            checks,
        )
    }

    @Test
    fun `reference fields check the RTT of their declared type`() {

        val middle = planner.storage(types[MIDDLE]).fieldLanes
        val holder = planner.storage(types[HOLDER]).fieldLanes

        assertEquals(RttCheck(keys[LEAF], 2, true), (middle[1] as DefinedReferenceLane).check)
        assertEquals(RttCheck(keys[BASE], 0, false), (holder[0] as DefinedReferenceLane).check)
        assertEquals(RttCheck(keys[BYTES], 0, true), (holder[1] as DefinedReferenceLane).check)
        assertEquals(AbstractReferenceLane::class, holder[2]::class)
        assertEquals(NumberLanes::class, holder[3]::class)
    }

    private fun lanes(fieldLanes: FieldLanes): Any {
        return when (fieldLanes) {
            is NumberLanes -> fieldLanes.lanes
            is DefinedReferenceLane -> listOf(fieldLanes.lane)
            is AbstractReferenceLane -> listOf(fieldLanes.lane)
        }
    }

    private fun definedTypes(module: String): List<DefinedType> {

        val decoded = WasmModuleDecoder(ModuleConfig(), moduleBytes(module))
            .getOrThrow { error -> IllegalStateException("$module should decode: $error") }

        return ModuleDeclarationsFactory(decoded).definedTypes
    }

    private companion object {
        const val BYTES = 0
        const val BASE = 1
        const val MIDDLE = 2
        const val LEAF = 3
        const val HOLDER = 4
    }
}
