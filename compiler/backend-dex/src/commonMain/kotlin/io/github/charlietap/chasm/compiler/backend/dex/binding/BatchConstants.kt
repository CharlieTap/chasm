package io.github.charlietap.chasm.compiler.backend.dex.binding

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal class BatchConstants {

    private val fields = LinkedHashMap<Resource.Longs, DexField>()
    private val order = ArrayList<Resource.Longs>()
    private var committed = 0
    private var ownerName: String? = null
    private var owner: ClassType? = null

    fun begin(owner: String) {
        fields.clear()
        order.clear()
        committed = 0
        ownerName = owner
        this.owner = ClassType(owner)
    }

    fun beginMethod() {
        while (order.size > committed) fields.remove(order.removeAt(order.size - 1))
    }

    fun finishMethod() {
        committed = order.size
    }

    fun field(
        owner: String,
        values: LongArray,
    ): DexField {

        if (owner != ownerName) begin(owner)
        val type = checkNotNull(this.owner)
        val longs = Resource.Longs(values)

        return fields.getOrPut(longs) {
            order += longs
            DexField(type, "$LONGS_FIELD_PREFIX${fields.size}", LONGS_TYPE)
        }
    }

    fun declare(builder: ClassBuilder) {

        beginMethod()
        if (fields.isEmpty()) return
        val owner = checkNotNull(owner)
        for (field in fields.values) builder.addStaticField(field, MemberAccess.Private)

        val groups = initialiserGroups()
        if (groups.size == 1) {
            builder.beginStaticInitializer(INITIALISER_REGISTERS).apply {
                initialise(groups.single())
                returnVoid()
                finish()
            }
            return
        }

        val helpers = groups.indices.map { group -> DexMethod(owner, "$LONGS_INITIALISER_PREFIX$group", DexType.Void, emptyList()) }
        groups.forEachIndexed { group, constants ->
            builder.beginStaticMethod(helpers[group], INITIALISER_REGISTERS, MemberAccess.Private).apply {
                initialise(constants)
                returnVoid()
                finish()
            }
        }
        builder.beginStaticInitializer(INITIALISER_REGISTERS).apply {
            helpers.forEach { helper -> invokeStatic(helper) }
            returnVoid()
            finish()
        }
    }

    private fun initialiserGroups(): List<List<Map.Entry<Resource.Longs, DexField>>> {

        val groups = ArrayList<List<Map.Entry<Resource.Longs, DexField>>>()
        var group = ArrayList<Map.Entry<Resource.Longs, DexField>>()
        var units = 0L
        for (entry in fields.entries) {
            val cost = FIELD_CODE_UNITS + entry.key.values.size * LONG_CODE_UNITS
            if (group.isNotEmpty() && units + cost > INITIALISER_CODE_UNITS) {
                groups += group
                group = ArrayList()
                units = 0
            }
            group += entry
            units += cost
        }
        if (group.isNotEmpty()) groups += group

        return groups
    }

    private fun CodeEmitter.initialise(constants: List<Map.Entry<Resource.Longs, DexField>>) {
        for ((longs, field) in constants) {
            const(LENGTH, longs.values.size)
            newArray(ARRAY, LENGTH, LONGS_TYPE)
            if (longs.values.isNotEmpty()) fillArrayData(ARRAY, longs.values)
            putStatic(ARRAY, field)
        }
    }

    private companion object {
        const val LONGS_FIELD_PREFIX = "longs"
        const val LONGS_INITIALISER_PREFIX = "initialiseLongs"
        const val INITIALISER_REGISTERS = 2
        const val INITIALISER_CODE_UNITS = 32_000L
        const val FIELD_CODE_UNITS = 16L
        const val LONG_CODE_UNITS = 4L
        val LONGS_TYPE = DexType.Long.array()
        val ARRAY = Reg(0)
        val LENGTH = Reg(1)
    }
}
