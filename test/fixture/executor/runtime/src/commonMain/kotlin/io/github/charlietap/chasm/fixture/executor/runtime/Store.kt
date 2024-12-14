package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

fun store(
    functions: MutableList<FunctionInstance> = mutableListOf(),
    tables: MutableList<TableInstance> = mutableListOf(),
    memories: MutableList<MemoryInstance> = mutableListOf(),
    globals: MutableList<GlobalInstance> = mutableListOf(),
    elements: MutableList<ElementInstance> = mutableListOf(),
    data: MutableList<DataInstance> = mutableListOf(),
) = Store(
    functions = functions,
    tables = tables,
    memories = memories,
    globals = globals,
    elements = elements,
    data = data,
)
