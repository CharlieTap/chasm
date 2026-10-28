package io.github.charlietap.chasm.ast.module

import kotlin.jvm.JvmInline

sealed interface Index {

    val idx: UInt

    @JvmInline
    value class LocalIndex(override val idx: UInt) : Index

    @JvmInline
    value class GlobalIndex(override val idx: UInt) : Index

    @JvmInline
    value class DataIndex(override val idx: UInt) : Index

    @JvmInline
    value class ElementIndex(override val idx: UInt) : Index

    @JvmInline
    value class FunctionIndex(override val idx: UInt) : Index

    @JvmInline
    value class LabelIndex(override val idx: UInt) : Index

    @JvmInline
    value class MemoryIndex(override val idx: UInt) : Index

    @JvmInline
    value class TableIndex(override val idx: UInt) : Index

    @JvmInline
    value class TagIndex(override val idx: UInt) : Index

    @JvmInline
    value class TypeIndex(override val idx: UInt) : Index

    @JvmInline
    value class FieldIndex(override val idx: UInt) : Index
}
