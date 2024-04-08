package io.github.charlietap.chasm.fixture

import io.github.charlietap.chasm.executor.gc.WeakReference
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

fun store(
    functions: MutableList<FunctionInstance> = mutableListOf(),
    tables: MutableList<TableInstance> = mutableListOf(),
    memories: MutableList<MemoryInstance> = mutableListOf(),
    globals: MutableList<GlobalInstance> = mutableListOf(),
    elements: MutableList<ElementInstance> = mutableListOf(),
    data: MutableList<DataInstance> = mutableListOf(),
    structs: MutableList<WeakReference<StructInstance>> = mutableListOf(),
    arrays: MutableList<WeakReference<ArrayInstance>> = mutableListOf(),
) = Store(
    functions = functions,
    tables = tables,
    memories = memories,
    globals = globals,
    elements = elements,
    data = data,
    structs = structs,
    arrays = arrays,
)
