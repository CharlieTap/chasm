package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.instance.ExceptionInstance
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.instance.TagInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

fun store(
    data: MutableList<DataInstance> = mutableListOf(),
    elements: MutableList<ElementInstance> = mutableListOf(),
    exceptions: MutableList<ExceptionInstance> = mutableListOf(),
    functions: MutableList<FunctionInstance> = mutableListOf(),
    globals: MutableList<GlobalInstance> = mutableListOf(),
    memories: MutableList<MemoryInstance> = mutableListOf(),
    tables: MutableList<TableInstance> = mutableListOf(),
    tags: MutableList<TagInstance> = mutableListOf(),
    instructions: MutableList<DispatchableInstruction> = mutableListOf(),
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
)
