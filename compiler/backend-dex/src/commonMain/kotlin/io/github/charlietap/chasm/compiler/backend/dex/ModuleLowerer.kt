package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ConstantExpressions
import io.github.charlietap.chasm.compiler.backend.analysis.gc.UnsupportedShape
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.compiler.backend.dex.abi.codeClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.constantsClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.binding.BoundResources
import io.github.charlietap.chasm.compiler.backend.dex.binding.boundResources
import io.github.charlietap.chasm.compiler.backend.dex.gc.ConstantsWriter
import io.github.charlietap.chasm.compiler.backend.dex.gc.ShapeWriter
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.DexterException
import io.github.charlietap.dexter.DexterFileFullException

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
        layout = ::partitionedLayout,
        functionLowerer = ::FunctionLowerer,
        entryLowerer = ::EntryLowerer,
        installLowerer = ::InstallLowerer,
    )

internal fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    layout: (ModulePlan) -> DexLayout,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {
    return try {

        val plan = ModulePlan(module, code, options)

        try {
            lowerFiles(plan, layout(plan), functionLowerer, entryLowerer, installLowerer)
        } catch (_: DexterFileFullException) {
            lowerFiles(plan, filePerBatchLayout(plan), functionLowerer, entryLowerer, installLowerer)
        }
    } catch (tooLarge: DexterException) {
        throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
    } catch (unsupported: Unsupported) {
        throw UnsupportedModule(unsupported.message.orEmpty())
    } catch (unsupported: UnsupportedShape) {
        throw UnsupportedModule(unsupported.message.orEmpty())
    }
}

private fun lowerFiles(
    plan: ModulePlan,
    layout: DexLayout,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val lowering = ModuleLowering(plan)
    val lowered = LoweredFunctions(plan.functionCount)
    val dexter = Dexter()
    val codeDex = ArrayList<ByteArray>(layout.files.size)
    var codeAndEntryFile: DexFile? = null
    layout.files.forEachIndexed { index, batches ->
        val file = layout.beginFile(index, dexter)
        try {
            for (batch in batches) lowerBatch(lowering, batch, file, lowered, functionLowerer)
        } catch (failure: Throwable) {
            file.abort()
            throw failure
        }
        if (layout.holdsEntries(index)) codeAndEntryFile = file else codeDex += file.finish()
    }

    val bound = boundResources(listOf(lowering.usedObjects), listOf(lowering.usedValues))

    return finishModule(plan, codeDex, codeAndEntryFile, lowered, lowering.chunkStarts, bound, entryLowerer, installLowerer)
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
    codeDex: List<ByteArray>,
    codeAndEntryFile: DexFile?,
    lowered: LoweredFunctions,
    chunkStarts: Array<IntArray?>,
    bound: BoundResources,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val reasons = lowered.reasons(plan.functions)
    if (reasons.isNotEmpty()) {
        codeAndEntryFile?.abort()
        throw UnsupportedFunctions(reasons)
    }
    return try {
        finishFiles(plan, codeDex, codeAndEntryFile, chunkStarts, bound, entryLowerer, installLowerer)
    } catch (failure: Throwable) {
        codeAndEntryFile?.abort()
        throw failure
    }
}

private fun finishFiles(
    plan: ModulePlan,
    codeDex: List<ByteArray>,
    codeAndEntryFile: DexFile?,
    chunkStarts: Array<IntArray?>,
    bound: BoundResources,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val compiled = LinkedHashMap<EntryKey, EntryCandidates>()
    for (function in plan.functions) {
        val type = plan.declarations.functionType(function.type)
        val key = EntryKey(plan.functionSignature(function.type), type)
        val method = plan.functionInvocation(function.index).method
        compiled.getOrPut(key) { EntryCandidates(type) }.functions += LoweredFunction(function.index.index, method)
    }

    val entries = arrayOfNulls<EntryRef>(plan.functionCount)
    val entryClasses = LinkedHashSet<String>()
    val groups = entryGroups(compiled, plan)
    val dexter = Dexter()
    val dex = ArrayList(codeDex)
    val entryFiles = if (codeAndEntryFile != null) listOf(groups) else groups.chunkedBy(ENTRY_FUNCTIONS_PER_FILE)
    for (members in entryFiles) {
        val file = codeAndEntryFile ?: dexter.beginFile()
        for (group in members) {
            val descriptor = entryLowerer(file, group, plan.packagePath)
            entryClasses += descriptor
            group.members.forEachIndexed { id, member -> entries[member.index] = EntryRef(descriptor, id) }
        }
        if (codeAndEntryFile == null) dex += file.finish()
    }

    val ordered = LinkedHashMap<Int, EntryRef>()
    entries.forEachIndexed { function, entry -> if (entry != null) ordered[function] = entry }
    val installer = installClass(plan.packagePath)
    val installFile = codeAndEntryFile ?: dexter.beginFile()
    val installEntries = installLowerer(installFile, installer, bound, ordered)
    val constantExpressions = ConstantExpressions(plan.module)
    val constants = if (constantExpressions.needed) constantsClass(plan.packagePath) else null
    if (constants != null) {
        ConstantsWriter(plan.module, plan.gc, constantExpressions).write(installFile, constants)
    }
    dex += installFile.finish()

    val splitStarts = HashMap<Int, IntArray>()
    chunkStarts.forEachIndexed { function, starts -> if (starts != null) splitStarts[function] = starts }

    val gc = plan.gc
    val shapeClasses = (gc.structs.map { shape -> shape.className } + gc.arrays.map { shape -> shape.className }).map(::binaryName)
    val shapeDex = if (shapeClasses.isEmpty()) {
        null
    } else {
        val file = dexter.beginFile()
        ShapeWriter().write(file, gc)
        file.finish()
    }

    return DexModule(
        dex = dex,
        installer = binaryName(installer),
        inlineMemory = plan.inlineMemory != null,
        chunkStarts = splitStarts,
        classesToInitialize = (plan.batches.indices.map { batch -> codeClass(plan.packagePath, batch) } + entryClasses + installEntries + installer + listOfNotNull(constants)).map(::binaryName),
        shapeDex = shapeDex,
        shapeClasses = shapeClasses,
        shapeTypes = List(gc.typeCount) { index -> gc.className(index)?.let(::binaryName) },
        constants = constants?.let(::binaryName),
    )
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
    val externResults: List<Boolean>,
) {
    constructor(
        signature: FunctionSignature,
        type: FunctionType,
    ) : this(
        signature.key,
        type.params.types.size,
        type.results.types.size,
        type.results.types.map(::isExternReference),
    )
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
            groups += EntryGroup(number = groups.size, type = type, signature = signature, members = members, referenceDescriptor = plan.referenceDescriptor)
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

internal const val ENTRY_GROUP_SIZE = 64

private const val ENTRY_FUNCTIONS_PER_FILE = 8_000

private fun isExternReference(
    type: ValueType,
): Boolean {
    val heap = (type as? ValueType.Reference)?.referenceType?.heapType
    return heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern
}
