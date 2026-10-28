package io.github.charlietap.chasm.cir

class OperandTransfer(
    val values: LongArray,
    val immediates: BooleanArray,
    val destinationSlotBase: Int,
) {
    init {
        require(values.size == immediates.size) { "operand transfer values and kinds differ in length" }
    }

    constructor(
        sources: List<TransferSource>,
        destinationSlotBase: Int,
    ) : this(
        values = LongArray(sources.size) { index ->
            when (val source = sources[index]) {
                is TransferSource.Immediate -> source.value
                is TransferSource.Slot -> source.slot.toLong()
            }
        },
        immediates = BooleanArray(sources.size) { index -> sources[index] is TransferSource.Immediate },
        destinationSlotBase = destinationSlotBase,
    )

    val size: Int
        get() = values.size

    fun sources(): List<TransferSource> = List(size) { index ->
        if (immediates[index]) TransferSource.Immediate(values[index]) else TransferSource.Slot(values[index].toInt())
    }

    override fun equals(
        other: Any?,
    ): Boolean =
        this === other ||
            other is OperandTransfer &&
            destinationSlotBase == other.destinationSlotBase &&
            values.contentEquals(other.values) &&
            immediates.contentEquals(other.immediates)

    override fun hashCode(): Int = 31 * (31 * values.contentHashCode() + immediates.contentHashCode()) + destinationSlotBase

    override fun toString(): String = "OperandTransfer(sources=${sources()}, destinationSlotBase=$destinationSlotBase)"
}

data class TailCallOperandTransfer(
    val wasm: OperandTransfer,
    val host: OperandTransfer,
)

sealed interface TransferSource {

    @CirTag("immediate")
    data class Immediate(
        val value: Long,
    ) : TransferSource

    @CirTag("slot")
    data class Slot(
        val slot: Int,
    ) : TransferSource
}
