package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.runtime.execution.AllocationMeter
import io.github.charlietap.chasm.runtime.execution.NestedInterpreter
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.reference.ReferenceStorage
import io.github.charlietap.chasm.runtime.stack.ValueStack
import kotlin.jvm.JvmField

class Store(
    val functions: MutableList<FunctionInstance> = [],
    val tables: MutableList<TableInstance> = [],
    val memories: MutableList<MemoryInstance> = [],
    val globals: MutableList<GlobalInstance> = [],
    val elements: MutableList<ElementInstance> = [],
    val data: MutableList<DataInstance> = [],
    val program: Program = Program(),
    val config: StoreConfig = StoreConfig(),
    val heap: WasmHeap = WasmHeap(config.limits.gcHeapBytes),
    val fuel: Fuel = Fuel(metered = config.meterFuel),
    val interrupt: Interrupt = Interrupt(enabled = config.interruptible),
) {
    // The most slots any one invocation's ValueStack may hold.
    val maxStackSlots: Int = ValueStack.maxCapacityFor(config.limits.stackBytes)

    var compiledCallDepth: Int = 0

    var invocationDepth: Int = 0

    var interpreter: NestedInterpreter? = null

    @JvmField
    var referenceStorage: ReferenceStorage? = null

    @JvmField
    val allocation: AllocationMeter = AllocationMeter()

    @PublishedApi
    @JvmField
    internal var liveStacks: LiveStack? = null
}
