package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
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

@InternalChasmApi
class Store(
    val functions: MutableList<FunctionInstance> = [],
    val tables: MutableList<TableInstance> = [],
    val memories: MutableList<MemoryInstance> = [],
    val globals: MutableList<GlobalInstance> = [],
    val elements: MutableList<ElementInstance> = [],
    val data: MutableList<DataInstance> = [],
    val program: Program = Program(),
    val config: RuntimeConfig = RuntimeConfig(),
    val heap: WasmHeap = WasmHeap(config.limits.memory.gcHeap?.value),
    val fuel: Fuel = Fuel(metered = config.limits.cpu is CpuLimits.Metered),
    val interrupt: Interrupt = Interrupt(enabled = config.interrupts),
) {
    val gcHeapLimit: Long? = config.limits.memory.gcHeap?.value

    val stackBytes: Long? = config.limits.memory.stack?.value ?: DEFAULT_STACK_BYTES

    // The most slots any one invocation's ValueStack may hold.
    val maxStackSlots: Int = ValueStack.maxCapacityFor(stackBytes)

    val memoryPageLimit: Int? = config.limits.memory.linearMemory?.let { bytes ->
        minOf(bytes.value / Bytes.PAGE, Int.MAX_VALUE.toLong()).toInt()
    }

    val tableElementLimit: Int? = config.limits.memory.tables?.let { bytes ->
        minOf(bytes.value / TABLE_ELEMENT_BYTES, Int.MAX_VALUE.toLong()).toInt()
    }

    val trapOnGrowthFailure: Boolean = config.limits.memory.onGrowthFailure == GrowthFailureStrategy.Trap

    init {
        val cpu = config.limits.cpu
        if (cpu is CpuLimits.Metered) {
            fuel.remaining = cpu.fuel.units
        }
    }

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

private const val TABLE_ELEMENT_BYTES = 8L
