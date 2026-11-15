package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.compiler.backend.dex.abi.codeClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.binding.BoundResources
import io.github.charlietap.chasm.compiler.backend.dex.binding.boundResources
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.DexterException

typealias ModuleLowerer = (Module, CirModule, DexOptions) -> DexModule

fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions = DexOptions(),
): DexModule =
    ModuleLowerer(
        module = module,
        code = code,
        options = options,
        functionLowerer = ::FunctionLowerer,
        entryLowerer = ::EntryLowerer,
        installLowerer = ::InstallLowerer,
    )

internal fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {
    return try {
        lowerModule(module, code, options, functionLowerer, entryLowerer, installLowerer)
    } catch (tooLarge: DexterException) {
        throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
    }
}

private fun lowerModule(
    module: Module,
    code: CirModule,
    options: DexOptions,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val lowering = ModuleLowering(module, code, options)
    val lowered = LoweredFunctions(lowering.plan.functionCount)
    val dexter = Dexter()
    val dex = lowering.batches.indices.map { batch ->
        dexFile(dexter) { file -> lowerBatch(lowering, batch, file, lowered, functionLowerer) }
    }

    val bound = boundResources(listOf(lowering.usedObjects), listOf(lowering.usedValues))

    return finishModule(lowering.plan, dexter, dex, lowered, lowering.chunkStarts, bound, entryLowerer, installLowerer)
}

internal fun lowerBatch(
    lowering: ModuleLowering,
    batch: Int,
    file: DexFile,
    lowered: LoweredFunctions,
    functionLowerer: FunctionLowerer,
) {

    if (batch == 0) lowering.addSignatureInterfaces(file)
    val owner = codeClass(lowering.packagePath, batch)
    val classBuilder = lowering.codeClassBuilder(file, owner)
    lowering.constants.begin(owner)

    for (function in lowering.batches[batch]) {
        val method = lowering.functionInvocation(function.index).method
        val reason = functionLowerer(lowering, function, classBuilder, method)
        lowered.record(function.index.index, reason)
    }

    lowering.constants.declare(classBuilder)
}

internal fun finishModule(
    plan: ModulePlan,
    dexter: Dexter,
    dex: List<ByteArray>,
    lowered: LoweredFunctions,
    chunkStarts: Array<IntArray?>,
    bound: BoundResources,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val reasons = lowered.reasons(plan.functions)
    if (reasons.isNotEmpty()) throw UnsupportedFunctions(reasons)

    val compiled = LinkedHashMap<EntryKey, EntryCandidates>()
    for (function in plan.functions) {
        val type = plan.declarations.functionType(function.type)
        val key = EntryKey(plan.functionSignature(function.type), type)
        val method = plan.functionInvocation(function.index).method
        compiled.getOrPut(key) { EntryCandidates(type) }.functions += LoweredFunction(function.index.index, method)
    }

    val entries = arrayOfNulls<EntryRef>(plan.functionCount)
    val entryDex = entryGroups(compiled, plan).chunkedBy(ENTRY_FUNCTIONS_PER_FILE).map { files ->
        dexFile(dexter) { file ->
            for (group in files) {
                val descriptor = entryLowerer(file, group, plan.packagePath)
                group.members.forEachIndexed { id, member -> entries[member.index] = EntryRef(descriptor, id) }
            }
        }
    }

    val ordered = LinkedHashMap<Int, EntryRef>()
    entries.forEachIndexed { function, entry -> if (entry != null) ordered[function] = entry }
    val installer = installClass(plan.packagePath)
    val install = installLowerer(dexter, installer, bound, ordered)

    val splitStarts = HashMap<Int, IntArray>()
    chunkStarts.forEachIndexed { function, starts -> if (starts != null) splitStarts[function] = starts }

    return DexModule(dex + entryDex + install, binaryName(installer), plan.inlineMemory != null, splitStarts)
}

internal class LoweredFunctions(
    functionCount: Int,
) {

    private val reasons = arrayOfNulls<String>(functionCount)

    fun record(
        function: Int,
        reason: String?,
    ) {
        reasons[function] = reason
    }

    fun reasons(functions: List<CirFunction>): Map<Int, String> {

        val unsupported = LinkedHashMap<Int, String>()
        for (function in functions) {
            val index = function.index.index
            reasons[index]?.let { reason -> unsupported[index] = reason }
        }

        return unsupported
    }
}

internal class EntryRef(
    val descriptor: String,
    val id: Int,
)

internal data class EntryCandidates(
    val type: FunctionType,
    val functions: MutableList<LoweredFunction> = ArrayList(),
)

internal data class EntryKey(
    val signature: String,
    val parameters: Int,
    val results: Int,
) {
    constructor(
        signature: FunctionSignature,
        type: FunctionType,
    ) : this(signature.key, type.params.types.size, type.results.types.size)
}

private fun entryGroups(
    compiled: Map<EntryKey, EntryCandidates>,
    plan: ModulePlan,
): List<EntryGroup> {
    val groups = ArrayList<EntryGroup>()
    for ((key, candidates) in compiled) {
        val type = candidates.type
        val signature = plan.signatureClass(key.signature)
        for (members in candidates.functions.chunked(ENTRY_GROUP_SIZE)) {
            groups += EntryGroup(number = groups.size, type = type, signature = signature, members = members)
        }
    }
    return groups
}

private fun List<EntryGroup>.chunkedBy(
    functions: Int,
): List<List<EntryGroup>> {
    val chunks = ArrayList<List<EntryGroup>>()
    var current = ArrayList<EntryGroup>()
    var count = 0
    for (group in this) {
        if (count + group.members.size > functions && current.isNotEmpty()) {
            chunks += current
            current = ArrayList()
            count = 0
        }
        current += group
        count += group.members.size
    }
    if (current.isNotEmpty()) chunks += current
    return chunks
}

private inline fun dexFile(
    dexter: Dexter,
    write: (DexFile) -> Unit,
): ByteArray {
    val file = dexter.beginFile()
    return try {
        write(file)
        file.finish()
    } catch (failure: Throwable) {
        file.abort()
        throw failure
    }
}

internal const val ENTRY_GROUP_SIZE = 64

private const val ENTRY_FUNCTIONS_PER_FILE = 8_000
