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

/**
 * A native local is accompanied by its incoming raw word until a canonical
 * numeric write occurs. This preserves reused slots on paths which never
 * execute that write, and after a frame helper produces an untyped reference.
 */
internal class KotlinRegionValues(private val output: StringBuilder, private val layout: KotlinRegionValueLayout) {
    fun declarations(indent: String) {
        layout.slots.forEach { slot ->
            val type = layout.nativeTypes[slot]
            if (type == null) {
                output.appendLine("${indent}var r$slot = vstack.getFrameSlot($slot)")
            } else {
                output.appendLine("${indent}var b$slot = vstack.getFrameSlot($slot)")
                output.appendLine("${indent}var r$slot = ${type.decode("b$slot")}")
                output.appendLine("${indent}var n$slot = false")
            }
        }
    }

    fun word(slot: Int): String = layout.nativeTypes[slot]?.let { "(if (n$slot) ${it.encode("r$slot")} else b$slot)" } ?: "r$slot"

    fun read(input: KotlinValueInput.Slot): String = if (input.type == layout.nativeTypes[input.slot]) "r${input.slot}" else input.type.decode(word(input.slot))

    fun save(indent: String) = layout.modified.forEach { output.appendLine("${indent}vstack.setFrameSlot($it, ${word(it)})") }

    fun reload(indent: String) = layout.slots.forEach { slot ->
        val type = layout.nativeTypes[slot]
        if (type == null) {
            output.appendLine("${indent}r$slot = vstack.getFrameSlot($slot)")
        } else {
            output.appendLine("${indent}b$slot = vstack.getFrameSlot($slot)")
            output.appendLine("${indent}r$slot = ${type.decode("b$slot")}")
            output.appendLine("${indent}n$slot = false")
        }
    }

    fun writeWord(slot: Int, expression: String, indent: String) {
        val type = layout.nativeTypes[slot]
        if (type == null) {
            output.appendLine("${indent}r$slot = $expression")
        } else {
            output.appendLine("${indent}b$slot = $expression")
            output.appendLine("${indent}r$slot = ${type.decode("b$slot")}")
            output.appendLine("${indent}n$slot = false")
        }
    }

    fun emit(index: Int, value: KotlinValueInstruction, indent: String) {
        val expression = value.operationExpression(index, ::read)
        val slot = value.destinationSlot
        if (slot == null) {
            output.appendLine("$indent$expression")
            return
        }
        val type = layout.nativeTypes[slot]
        if (type == null || value.destinationType == null || !value.canonicalResult) {
            writeWord(slot, value.resultType.encode(expression), indent)
        } else {
            check(type == value.destinationType.localType())
            val native = if (value.resultType == type) expression else type.decode(value.resultType.encode(expression))
            output.appendLine("${indent}r$slot = $native")
            output.appendLine("${indent}n$slot = true")
        }
    }
}
