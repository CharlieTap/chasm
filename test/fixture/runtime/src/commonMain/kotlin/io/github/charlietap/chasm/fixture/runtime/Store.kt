package io.github.charlietap.chasm.fixture.runtime

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
import io.github.charlietap.chasm.runtime.store.Store

fun store(
    data: MutableList<DataInstance> = [],
    elements: MutableList<ElementInstance> = [],
    exceptions: MutableList<ExceptionInstance> = [],
    functions: MutableList<FunctionInstance> = [],
    globals: MutableList<GlobalInstance> = [],
    memories: MutableList<MemoryInstance> = [],
    tables: MutableList<TableInstance> = [],
    tags: MutableList<TagInstance> = [],
    instructions: MutableList<DispatchableInstruction> = [],
    structs: MutableList<StructInstance?> = [],
    arrays: MutableList<ArrayInstance?> = [],
    heap: Heap = heap(),
) = Store(
    data = data,
    elements = elements,
    exceptions = exceptions,
    functions = functions,
    globals = globals,
    memories = memories,
    tables = tables,
    tags = tags,
    instructions = instructions,
    structs = structs,
    arrays = arrays,
    heap = heap,
)
