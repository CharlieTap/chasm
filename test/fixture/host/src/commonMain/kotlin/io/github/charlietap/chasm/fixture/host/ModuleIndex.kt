package io.github.charlietap.chasm.fixture.host

import io.github.charlietap.chasm.host.ModuleIndex

fun functionIndex(
    index: Int = 0,
) = ModuleIndex.FunctionIndex(index)

fun tableIndex(
    index: Int = 0,
) = ModuleIndex.TableIndex(index)

fun memoryIndex(
    index: Int = 0,
) = ModuleIndex.MemoryIndex(index)

fun globalIndex(
    index: Int = 0,
) = ModuleIndex.GlobalIndex(index)

fun tagIndex(
    index: Int = 0,
) = ModuleIndex.TagIndex(index)
