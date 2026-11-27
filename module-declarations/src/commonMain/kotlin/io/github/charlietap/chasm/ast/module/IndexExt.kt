package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
fun Index.LocalIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.GlobalIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.DataIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.ElementIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.FunctionIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.LabelIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.MemoryIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.TableIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.TagIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.TypeIndex.toInt(): Int = idx.toRuntimeIndex()

@InternalChasmApi
fun Index.FieldIndex.toInt(): Int = idx.toRuntimeIndex()

private fun UInt.toRuntimeIndex(): Int {
    check(this <= Int.MAX_VALUE.toUInt()) {
        "module index exceeds the runtime index range: $this"
    }
    return toInt()
}
