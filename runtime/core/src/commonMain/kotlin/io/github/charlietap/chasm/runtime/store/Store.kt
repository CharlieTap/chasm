package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.HostInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.program.Program

class Store(
    val functions: MutableList<FunctionInstance> = [],
    val tables: MutableList<TableInstance> = [],
    val memories: MutableList<MemoryInstance> = [],
    val globals: MutableList<GlobalInstance> = [],
    val elements: MutableList<ElementInstance> = [],
    val data: MutableList<DataInstance> = [],
    val hosts: MutableList<HostInstance> = [],
    val program: Program = Program(),
    val heap: WasmHeap = WasmHeap(),
)
