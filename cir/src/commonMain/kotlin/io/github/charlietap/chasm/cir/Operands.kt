package io.github.charlietap.chasm.cir

import kotlin.jvm.JvmInline

@JvmInline
value class InstructionIndex(
    val index: Int,
)

@JvmInline
value class FunctionIndex(
    val index: Int,
)

@JvmInline
value class TypeIndex(
    val index: Int,
)

@JvmInline
value class MemoryIndex(
    val index: Int,
)

@JvmInline
value class TableIndex(
    val index: Int,
)

@JvmInline
value class GlobalIndex(
    val index: Int,
)

@JvmInline
value class TagIndex(
    val index: Int,
)

@JvmInline
value class DataIndex(
    val index: Int,
)

@JvmInline
value class ElementIndex(
    val index: Int,
)

@JvmInline
value class MemArg(
    val offset: Int,
)

typealias F32 = Float

typealias F64 = Double

enum class PackedType {
    I8,
    I16,
}
