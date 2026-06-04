package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.runtime.Heap
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

data class Store(
    val functions: MutableList<FunctionInstance> = [],
    val tables: MutableList<TableInstance> = [],
    val memories: MutableList<MemoryInstance> = [],
    val tags: MutableList<TagInstance> = [],
    val globals: MutableList<GlobalInstance> = [],
    val elements: MutableList<ElementInstance> = [],
    val data: MutableList<DataInstance> = [],
    val exceptions: MutableList<ExceptionInstance> = [],
    val instructions: MutableList<DispatchableInstruction> = [],
    val structs: MutableList<StructInstance?> = [],
    val arrays: MutableList<ArrayInstance?> = [],
    val rttCache: MutableMap<DefinedType, RTT> = mutableMapOf(),
    val heap: Heap = Heap(),
)
