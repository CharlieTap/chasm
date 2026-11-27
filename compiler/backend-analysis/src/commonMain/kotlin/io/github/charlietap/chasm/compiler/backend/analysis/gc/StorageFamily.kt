package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
enum class LaneKind {
    INT,
    REFERENCE,
}

@InternalChasmApi
data class Lane(
    val kind: LaneKind,
    val index: Int,
    val spilled: Boolean,
) {

    fun fieldName(): String {
        return if (kind == LaneKind.INT) "i$index" else "r$index"
    }
}

@InternalChasmApi
class StorageClass internal constructor(
    val name: String,
    val superclass: StorageClass?,
    val lanes: List<Lane>,
    val final: Boolean,
) {
    internal val mutableSubclasses = ArrayList<StorageClass>()

    val subclasses: List<StorageClass>
        get() = mutableSubclasses

    val intLanes: Int = (superclass?.intLanes ?: 0) + lanes.count { lane -> lane.kind == LaneKind.INT }

    val referenceLanes: Int = (superclass?.referenceLanes ?: 0) + lanes.count { lane -> lane.kind == LaneKind.REFERENCE }

    fun declaringClass(lane: Lane): StorageClass {

        if (lane.spilled) {
            return this
        }

        var storageClass = this
        while (lane !in storageClass.lanes) {
            storageClass = checkNotNull(storageClass.superclass) { "$name has no lane $lane" }
        }

        return storageClass
    }
}

@InternalChasmApi
class StorageFamily(
    val inlineLanes: Int,
    val tailLevels: Int,
) {
    private val classesByName = LinkedHashMap<String, StorageClass>()

    val roots: List<StorageClass> = listOf(LaneKind.INT, LaneKind.REFERENCE).map { kind ->
        addTree(ROOT_NAME, null, listOf(kind))
    }

    val classes: Collection<StorageClass>
        get() = classesByName.values

    fun storageClass(name: String): StorageClass {
        return classesByName[name] ?: error("$name is not a storage class")
    }

    private fun addTree(
        superclassName: String,
        superclass: StorageClass?,
        kinds: List<LaneKind>,
    ): StorageClass {

        val name = superclassName + "_" + kinds.joinToString("") { kind -> if (kind == LaneKind.INT) "I" else "R" }
        val storageClass = addClass(name, superclass, kinds, final = false)

        if (storageClass.intLanes + storageClass.referenceLanes < inlineLanes) {
            for (pair in LANE_PAIRS) {
                addTree(name, storageClass, pair)
            }
        } else {
            addTail(name, storageClass, 1)
        }

        return storageClass
    }

    private fun addTail(
        leafName: String,
        superclass: StorageClass,
        level: Int,
    ) {
        if (level > tailLevels) {
            addClass(leafName + "_X", superclass, emptyList(), final = true)
        } else {
            addTail(leafName, addClass(leafName + "_T" + level, superclass, TAIL_LEVEL_LANES, final = false), level + 1)
        }
    }

    private fun addClass(
        name: String,
        superclass: StorageClass?,
        kinds: List<LaneKind>,
        final: Boolean,
    ): StorageClass {

        var nextInt = superclass?.intLanes ?: 0
        var nextReference = superclass?.referenceLanes ?: 0
        val lanes = kinds.map { kind ->
            when (kind) {
                LaneKind.INT -> Lane(kind, nextInt++, spilled = false)
                LaneKind.REFERENCE -> Lane(kind, nextReference++, spilled = false)
            }
        }

        val storageClass = StorageClass("L$STORAGE_PACKAGE$name;", superclass, lanes, final)
        superclass?.mutableSubclasses?.add(storageClass)
        classesByName[storageClass.name] = storageClass

        return storageClass
    }

    private companion object {
        const val ROOT_NAME = "S"
        val LANE_PAIRS = listOf(
            listOf(LaneKind.INT, LaneKind.INT),
            listOf(LaneKind.INT, LaneKind.REFERENCE),
            listOf(LaneKind.REFERENCE, LaneKind.REFERENCE),
        )
        val TAIL_LEVEL_LANES = listOf(LaneKind.INT, LaneKind.REFERENCE)
    }
}

internal const val STORAGE_PACKAGE = "io/github/charlietap/chasm/runtime/compiled/gc/storage/"

@InternalChasmApi
val STORAGE_FAMILY = StorageFamily(inlineLanes = 7, tailLevels = 16)
