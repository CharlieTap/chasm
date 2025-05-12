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
    val functions: MutableList<FunctionInstance> = mutableListOf(),
    val tables: MutableList<TableInstance> = mutableListOf(),
    val memories: MutableList<MemoryInstance> = mutableListOf(),
    val tags: MutableList<TagInstance> = mutableListOf(),
    val globals: MutableList<GlobalInstance> = mutableListOf(),
    val elements: MutableList<ElementInstance> = mutableListOf(),
    val data: MutableList<DataInstance> = mutableListOf(),
    val exceptions: MutableList<ExceptionInstance> = mutableListOf(),
    val instructions: MutableList<DispatchableInstruction> = mutableListOf(),
    val structs: MutableList<StructInstance?> = mutableListOf(),
    val arrays: MutableList<ArrayInstance?> = mutableListOf(),
    val rttCache: MutableMap<DefinedType, RTT> = mutableMapOf(),
    val heap: Heap = Heap(),
)
