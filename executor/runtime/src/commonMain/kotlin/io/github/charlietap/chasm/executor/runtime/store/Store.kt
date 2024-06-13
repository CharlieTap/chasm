package io.github.charlietap.chasm.executor.runtime.store

import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.weakref.WeakReference

data class Store(
    val functions: MutableList<FunctionInstance> = mutableListOf(),
    val tables: MutableList<TableInstance> = mutableListOf(),
    val memories: MutableList<MemoryInstance> = mutableListOf(),
    val globals: MutableList<GlobalInstance> = mutableListOf(),
    val elements: MutableList<ElementInstance> = mutableListOf(),
    val data: MutableList<DataInstance> = mutableListOf(),
    val structs: MutableList<WeakReference<StructInstance>> = mutableListOf(),
    val arrays: MutableList<WeakReference<ArrayInstance>> = mutableListOf(),
)
