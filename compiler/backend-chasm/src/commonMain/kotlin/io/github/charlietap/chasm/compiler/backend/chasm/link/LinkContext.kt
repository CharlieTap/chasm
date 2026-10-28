package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.chasm.NO_POSITIONS
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.compiler.backend.chasm.unlinkedCheckpointsBefore
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.store.Fuel
import io.github.charlietap.chasm.runtime.store.Interrupt
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType

internal class LinkContext(
    val instance: ModuleInstance,
    val runtimeTypes: RuntimeTypeMap,
    val functionAddresses: IntArray,
    val functions: Array<FunctionInstance>,
    val tables: Array<TableInstance>,
    val memories: Array<MemoryInstance>,
    val globals: Array<GlobalInstance>,
    val elements: Array<ElementInstance>,
    val data: Array<DataInstance>,
    val definedTypes: List<DefinedType>,
    val fuel: Fuel?,
    val interrupt: Interrupt?,
) {
    var entryIp = 0

    private var unlinkedCheckpoints = NO_POSITIONS

    private var firstUnlinkedCheckpoint = Int.MAX_VALUE
    private var secondUnlinkedCheckpoint = Int.MAX_VALUE

    var frameSlots = 0

    var nextIp = 0

    val pendingWasmCalls = ArrayList<PendingWasmCall>()

    fun deferWasmCall(
        call: PendingWasmCall,
    ): DispatchableInstruction {
        pendingWasmCalls.add(call)
        return PENDING_WASM_CALL
    }

    val linksCheckpoints: Boolean
        get() = fuel != null || interrupt != null

    fun ip(
        position: InstructionIndex,
    ): Int = ip(position.index)

    fun ip(
        position: Int,
    ): Int = when {
        position <= firstUnlinkedCheckpoint -> entryIp + position
        position <= secondUnlinkedCheckpoint -> entryIp + position - 1
        else -> entryIp + position - unlinkedCheckpointsBefore(unlinkedCheckpoints, position, from = 2)
    }

    fun beginFunction(
        entryIp: Int,
        function: CirFunction,
    ) {
        this.entryIp = entryIp
        frameSlots = function.frame.frameSlots
        val unlinked = if (linksCheckpoints) NO_POSITIONS else function.checkpoints
        unlinkedCheckpoints = unlinked
        firstUnlinkedCheckpoint = if (unlinked.isEmpty()) Int.MAX_VALUE else unlinked[0]
        secondUnlinkedCheckpoint = if (unlinked.size < 2) Int.MAX_VALUE else unlinked[1]
    }

    fun programSize(
        function: CirFunction,
    ): Int =
        function.instructions.size - if (linksCheckpoints) 0 else function.checkpoints.size

    fun forWorker(): LinkContext = LinkContext(
        instance = instance,
        runtimeTypes = runtimeTypes,
        functionAddresses = functionAddresses,
        functions = functions,
        tables = tables,
        memories = memories,
        globals = globals,
        elements = elements,
        data = data,
        definedTypes = definedTypes,
        fuel = fuel,
        interrupt = interrupt,
    )

    private val structDefaults = arrayOfNulls<LongArray>(definedTypes.size)
    private val arrayDefaults = LongArray(definedTypes.size)
    private val arrayDefaultsResolved = BooleanArray(definedTypes.size)

    fun structDefaults(
        typeIndex: Int,
    ): LongArray = structDefaults[typeIndex] ?: run {
        val fields = checkNotNull(definedTypes[typeIndex].asSubType.compositeType.structType()) {
            "type $typeIndex is not a struct type"
        }.fields
        LongArray(fields.size) { index -> fields[index].default() }.also { defaults -> structDefaults[typeIndex] = defaults }
    }

    fun arrayDefault(
        typeIndex: Int,
    ): Long {
        if (arrayDefaultsResolved[typeIndex]) return arrayDefaults[typeIndex]
        val array = checkNotNull(definedTypes[typeIndex].asSubType.compositeType.arrayType()) {
            "type $typeIndex is not an array type"
        }
        return array.fieldType.default().also { default ->
            arrayDefaults[typeIndex] = default
            arrayDefaultsResolved[typeIndex] = true
        }
    }

    fun typeTest(
        type: ReferenceType,
    ): ReferenceTypeTest = ReferenceTypeTest.from(type.toRuntime(), runtimeTypes)
}

private val PENDING_WASM_CALL = DispatchableInstruction { _, _, _ -> error("pending Wasm call cannot be dispatched") }

internal fun createLinkContext(
    store: Store,
    instance: ModuleInstance,
    definedTypes: List<DefinedType>,
): LinkContext = LinkContext(
    instance = instance,
    runtimeTypes = instance.runtimeTypes,
    functionAddresses = IntArray(instance.functionAddresses.size) { index -> instance.functionAddresses[index].address },
    functions = Array(instance.functionAddresses.size) { index -> store.function(instance.functionAddresses[index]) },
    tables = Array(instance.tableAddresses.size) { index -> store.table(instance.tableAddresses[index]) },
    memories = Array(instance.memAddresses.size) { index -> store.memory(instance.memAddresses[index]) },
    globals = Array(instance.globalAddresses.size) { index -> store.global(instance.globalAddresses[index]) },
    elements = Array(instance.elemAddresses.size) { index -> store.element(instance.elemAddresses[index]) },
    data = Array(instance.dataAddresses.size) { index -> store.data(instance.dataAddresses[index]) },
    definedTypes = definedTypes,
    fuel = store.fuel.takeIf { it.metered },
    interrupt = store.interrupt.takeIf { it.enabled },
)
