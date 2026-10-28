package io.github.charlietap.chasm.cir

data class CirFunction(
    val index: FunctionIndex,
    val type: TypeIndex,
    val frame: Frame,
    val instructions: Array<Instruction>,
    val checkpoints: IntArray = IntArray(0),
    val exceptions: ExceptionTable? = null,
    val provenance: Provenance? = null,
) {
    override fun equals(
        other: Any?,
    ): Boolean =
        this === other ||
            other is CirFunction &&
            index == other.index &&
            type == other.type &&
            frame == other.frame &&
            instructions.contentEquals(other.instructions) &&
            checkpoints.contentEquals(other.checkpoints) &&
            exceptions == other.exceptions &&
            provenance == other.provenance

    override fun hashCode(): Int {
        var result = index.hashCode()
        result = 31 * result + instructions.contentHashCode()
        result = 31 * result + checkpoints.contentHashCode()
        return result
    }
}

data class Frame(
    val frameSlots: Int,
    val activationHeaderSlot: Int,
    val locals: List<ValueType> = emptyList(),
    val initialisedLocalCount: Int = 0,
) {
    init {
        require(initialisedLocalCount in 0..locals.size) { "initialised local count $initialisedLocalCount exceeds ${locals.size} locals" }
    }
}

data class ExceptionTable(
    val regions: List<ExceptionRegion>,
    val tailCalls: IntArray,
) {
    override fun equals(
        other: Any?,
    ): Boolean =
        this === other || other is ExceptionTable && regions == other.regions && tailCalls.contentEquals(other.tailCalls)

    override fun hashCode(): Int = 31 * regions.hashCode() + tailCalls.contentHashCode()
}

data class ExceptionRegion(
    val start: InstructionIndex,
    val end: InstructionIndex,
    val parent: Int?,
    val catches: List<ExceptionCatch>,
)

data class ExceptionCatch(
    val tag: TagIndex?,
    val target: InstructionIndex,
    val payloadSlots: IntArray,
    val exceptionReference: Boolean,
    val stackSlots: Int,
) {
    override fun equals(
        other: Any?,
    ): Boolean =
        this === other ||
            other is ExceptionCatch &&
            tag == other.tag &&
            target == other.target &&
            payloadSlots.contentEquals(other.payloadSlots) &&
            exceptionReference == other.exceptionReference &&
            stackSlots == other.stackSlots

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + payloadSlots.contentHashCode()
        return result
    }
}

data class Provenance(
    val sourceStart: IntArray,
    val sourceEnd: IntArray,
) {
    override fun equals(
        other: Any?,
    ): Boolean =
        this === other ||
            other is Provenance &&
            sourceStart.contentEquals(other.sourceStart) &&
            sourceEnd.contentEquals(other.sourceEnd)

    override fun hashCode(): Int = 31 * sourceStart.contentHashCode() + sourceEnd.contentHashCode()
}
