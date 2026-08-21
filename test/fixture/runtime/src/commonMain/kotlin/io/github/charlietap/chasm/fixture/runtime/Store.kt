package io.github.charlietap.chasm.fixture.runtime

import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.HostInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store

fun store(
    data: MutableList<DataInstance> = [],
    elements: MutableList<ElementInstance> = [],
    functions: MutableList<FunctionInstance> = [],
    globals: MutableList<GlobalInstance> = [],
    memories: MutableList<MemoryInstance> = [],
    tables: MutableList<TableInstance> = [],
    program: Program = Program(),
    hosts: MutableList<HostInstance> = [],
) = Store(
    data = data,
    elements = elements,
    functions = functions,
    globals = globals,
    memories = memories,
    tables = tables,
    hosts = hosts,
    program = program,
)
