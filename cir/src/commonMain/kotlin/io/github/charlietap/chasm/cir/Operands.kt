package io.github.charlietap.chasm.cir

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
@JvmInline
value class InstructionIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class FunctionIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class TypeIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class MemoryIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class TableIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class GlobalIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class TagIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class DataIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class ElementIndex(
    val index: Int,
)

@InternalChasmApi
@JvmInline
value class MemArg(
    val offset: Int,
)

internal typealias F32 = Float

internal typealias F64 = Double

@InternalChasmApi
enum class PackedType {
    I8,
    I16,
}
