package io.github.charlietap.chasm.ast.module

fun Index.LocalIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.GlobalIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.DataIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.ElementIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.FunctionIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.LabelIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.MemoryIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.TableIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.TagIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.TypeIndex.toInt(): Int = idx.toRuntimeIndex()

fun Index.FieldIndex.toInt(): Int = idx.toRuntimeIndex()

private fun UInt.toRuntimeIndex(): Int {
    check(this <= Int.MAX_VALUE.toUInt()) {
        "module index exceeds the runtime index range: $this"
    }
    return toInt()
}
