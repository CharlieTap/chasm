package io.github.charlietap.chasm.compiler.kotlin

internal data class KotlinRegionValueLayout(
    val slots: List<Int>,
    val modified: List<Int>,
    val nativeTypes: Map<Int, KotlinValueType>,
    val mixedSlotCount: Int,
)

private fun KotlinValueType.localType(): KotlinValueType = if (this == KotlinValueType.BOOL) KotlinValueType.I32 else this

internal fun regionValueLayout(
    indices: List<Int>,
    values: List<KotlinValueInstruction?>,
    branches: List<KotlinBranch?>,
    copies: List<KotlinCopies?>,
    typed: Boolean,
): KotlinRegionValueLayout {
    val slots = mutableSetOf<Int>()
    val modified = mutableSetOf<Int>()
    val types = mutableMapOf<Int, MutableSet<KotlinValueType>>()

    fun constrain(slot: Int, type: KotlinValueType) {
        if (typed) types.getOrPut(slot) { mutableSetOf() }.add(type.localType())
    }

    fun inputs(inputs: List<KotlinValueInput>, raw: Boolean = false) {
        inputs.filterIsInstance<KotlinValueInput.Slot>().forEach { input ->
            slots.add(input.slot)
            if (!raw && !input.rawWord) constrain(input.slot, input.type)
        }
    }

    fun write(slot: Int, type: KotlinValueType? = null) {
        slots.add(slot)
        modified.add(slot)
        if (type != null) constrain(slot, type)
    }

    fun copy(copy: KotlinCopies) {
        inputs(copy.sources, raw = true)
        copy.destinations.forEach { write(it) }
    }
    indices.forEach { index ->
        values[index]?.let { value ->
            inputs(value.inputs)
            value.destinationSlot?.let { write(it, value.destinationType) }
        }
        copies[index]?.let(::copy)
        branches[index]?.let { branch ->
            branch.condition?.let { inputs(it.inputs) }
            branch.copies?.let(::copy)
        }
    }
    require(slots.all { it >= 0 }) { "Generated regions require nonnegative frame slots" }
    val native = types.mapNotNull { (slot, kinds) ->
        kinds.singleOrNull()?.takeIf { it == KotlinValueType.I32 || it == KotlinValueType.F32 || it == KotlinValueType.F64 }?.let { slot to it }
    }.toMap()
    return KotlinRegionValueLayout(slots.sorted(), modified.sorted(), native, types.count { it.value.size > 1 })
}

/** The native representation of a canonical numeric write, if eligible. */
internal fun KotlinRegionValueLayout.nativeWriteType(value: KotlinValueInstruction): KotlinValueType? {
    if (!value.canonicalResult || value.destinationType == null) return null
    val type = nativeTypes[value.destinationSlot] ?: return null
    check(type == value.destinationType.localType())
    return type
}

/**
 * Exactly one raw word survives control-flow joins for each physical slot.
 * Native values are immutable temporaries produced within a basic block, so
 * their types are known without retaining a second representation or a flag
 * across a loop. Copies, helper reloads and new blocks invalidate the cache.
 */
internal class KotlinRegionValues(private val output: StringBuilder, private val layout: KotlinRegionValueLayout) {
    private data class NativeValue(val type: KotlinValueType, val name: String)

    private val nativeValues = mutableMapOf<Int, NativeValue>()

    fun declarations(indent: String) {
        layout.slots.forEach { slot -> output.appendLine("${indent}var r$slot = vstack.getFrameSlot($slot)") }
    }

    fun beginBlock() = nativeValues.clear()

    fun word(slot: Int): String = "r$slot"

    fun read(input: KotlinValueInput.Slot): String = nativeValues[input.slot]
        ?.takeIf { !input.rawWord && it.type == input.type }
        ?.name
        ?: input.type.decode(word(input.slot))

    fun save(indent: String) = layout.modified.forEach { output.appendLine("${indent}vstack.setFrameSlot($it, ${word(it)})") }

    fun reload(indent: String) {
        nativeValues.clear()
        layout.slots.forEach { output.appendLine("${indent}r$it = vstack.getFrameSlot($it)") }
    }

    fun writeWord(slot: Int, expression: String, indent: String) {
        output.appendLine("${indent}r$slot = $expression")
        nativeValues.remove(slot)
    }

    fun emit(index: Int, value: KotlinValueInstruction, indent: String) {
        val expression = value.operationExpression(index, ::read)
        val slot = value.destinationSlot
        if (slot == null) {
            output.appendLine("$indent$expression")
            return
        }
        val type = layout.nativeWriteType(value)
        if (type == null) {
            writeWord(slot, value.resultType.encode(expression), indent)
        } else {
            val native = if (value.resultType == type) expression else type.decode(value.resultType.encode(expression))
            val name = "v$index"
            output.appendLine("${indent}val $name = $native")
            output.appendLine("${indent}r$slot = ${type.encode(name)}")
            nativeValues[slot] = NativeValue(type, name)
        }
    }
}
