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
class StorageBranch(
    val root: List<LaneKind>,
    val firstSteps: List<List<LaneKind>>,
    val inlineLanes: Int,
    val tailLevels: Int,
)

@InternalChasmApi
class StorageFamily(
    branches: List<StorageBranch>,
) {
    private val classesByName = LinkedHashMap<String, StorageClass>()

    val roots: List<StorageClass> = branches.map { branch ->
        val path = letters(branch.root)
        val root = addClass(path, null, branch.root, final = false)
        addSteps(branch, path, root, branch.firstSteps)
        root
    }

    val classes: Collection<StorageClass>
        get() = classesByName.values

    fun storageClass(name: String): StorageClass {
        return classesByName[name] ?: error("$name is not a storage class")
    }

    private fun addSteps(
        branch: StorageBranch,
        path: String,
        storageClass: StorageClass,
        steps: List<List<LaneKind>>,
    ) {
        if (storageClass.intLanes + storageClass.referenceLanes < branch.inlineLanes) {
            for (step in steps) {
                val stepPath = path + PATH_SEPARATOR + letters(step)
                addSteps(branch, stepPath, addClass(stepPath, storageClass, step, final = false), LANE_PAIRS)
            }
        } else {
            addTail(branch, path, storageClass, 1)
        }
    }

    private fun addTail(
        branch: StorageBranch,
        leafPath: String,
        superclass: StorageClass,
        level: Int,
    ) {

        val steps = leafPath + PATH_SEPARATOR + letters(TAIL_LEVEL_LANES) + REPEAT_MARKER

        if (level > branch.tailLevels) {
            addClass(steps + branch.tailLevels + SPILL_SUFFIX, superclass, emptyList(), final = true)
        } else {
            addTail(branch, leafPath, addClass(steps + level, superclass, TAIL_LEVEL_LANES, final = false), level + 1)
        }
    }

    private fun addClass(
        path: String,
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

        val name = "Struct_I${nextInt}_R${nextReference}__$path"
        val storageClass = StorageClass("L$STORAGE_PACKAGE$name;", superclass, lanes, final)
        superclass?.mutableSubclasses?.add(storageClass)
        classesByName[storageClass.name] = storageClass

        return storageClass
    }

    private fun letters(kinds: List<LaneKind>): String {
        return kinds.joinToString("") { kind -> if (kind == LaneKind.INT) "I" else "R" }
    }

    private companion object {
        const val PATH_SEPARATOR = "_"
        const val REPEAT_MARKER = "x"
        const val SPILL_SUFFIX = "_Spill"
    }
}

internal const val STORAGE_PACKAGE = "io/github/charlietap/chasm/runtime/compiled/gc/storage/"

private val LANE_SINGLES = listOf(
    listOf(LaneKind.INT),
    listOf(LaneKind.REFERENCE),
)

private val LANE_PAIRS = listOf(
    listOf(LaneKind.INT, LaneKind.INT),
    listOf(LaneKind.INT, LaneKind.REFERENCE),
    listOf(LaneKind.REFERENCE, LaneKind.REFERENCE),
)

private val TAIL_LEVEL_LANES = listOf(LaneKind.INT, LaneKind.REFERENCE)

private val KOTLIN_HEADER = listOf(LaneKind.INT, LaneKind.REFERENCE, LaneKind.REFERENCE, LaneKind.REFERENCE)

@InternalChasmApi
val STORAGE_FAMILY = StorageFamily(
    listOf(
        StorageBranch(root = listOf(LaneKind.INT), firstSteps = LANE_PAIRS, inlineLanes = 5, tailLevels = 18),
        StorageBranch(root = listOf(LaneKind.REFERENCE), firstSteps = LANE_PAIRS, inlineLanes = 5, tailLevels = 18),
        StorageBranch(root = KOTLIN_HEADER, firstSteps = LANE_SINGLES + LANE_PAIRS, inlineLanes = 7, tailLevels = 16),
    ),
)
