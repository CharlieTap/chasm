@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.compiled.format.writeTrapSites
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.BuilderCall
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.DeclarationCallGenerator
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.DiscardingOutputStream
import io.github.charlietap.chasm.compiler.backend.analysis.entryShapeGroups
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ConstantExpressions
import io.github.charlietap.chasm.compiler.backend.analysis.gc.UnsupportedShape
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.binding.BoundResources
import io.github.charlietap.chasm.compiler.backend.dex.binding.boundResources
import io.github.charlietap.chasm.compiler.backend.dex.gc.ConstantsWriter
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.DexterException
import io.github.charlietap.dexter.DexterFileFullException

typealias ModuleLowerer = (Module, CirModule, DexOptions, ModuleDeclarations, Map<Int, FunctionTrapSites>, DexResources) -> DexModule

fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    declarations: ModuleDeclarations,
    trapSites: Map<Int, FunctionTrapSites>,
    resources: DexResources,
): DexModule {
    return ModuleLowerer(
        module = module,
        code = code,
        options = options,
        declarations = declarations,
        trapSites = trapSites,
        resources = resources,
        limits = MetadataFiles.DEX_LIMITS,
        layout = ::partitionedLayout,
        functionLowerer = ::FunctionLowerer,
        entryLowerer = ::EntryLowerer,
        installLowerer = ::InstallLowerer,
    )
}

fun lowerWithSeparateMetadata(
    module: Module,
    code: CirModule,
    options: DexOptions,
    declarations: ModuleDeclarations,
    resources: DexResources,
    limits: PoolLimits,
): DexModule {
    return ModuleLowerer(
        module = module,
        code = code,
        options = options,
        declarations = declarations,
        trapSites = emptyMap(),
        resources = resources,
        limits = limits,
        layout = ::filePerBatchLayout,
        functionLowerer = ::FunctionLowerer,
        entryLowerer = ::EntryLowerer,
        installLowerer = ::InstallLowerer,
    )
}

internal fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    declarations: ModuleDeclarations,
    trapSites: Map<Int, FunctionTrapSites>,
    resources: DexResources,
    limits: PoolLimits,
    layout: (ModulePlan) -> DexLayout,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {
    return try {

        val plan = ModulePlan(module, code, options)
        val metadata = ModuleMetadata(declarationCalls(plan, declarations, resources), trapSites, resources, limits)

        try {
            lowerFiles(plan, layout(plan), metadata, functionLowerer, entryLowerer, installLowerer)
        } catch (_: DexterFileFullException) {
            lowerFiles(plan, filePerBatchLayout(plan), metadata, functionLowerer, entryLowerer, installLowerer)
        }
    } catch (tooLarge: DexterException) {
        throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
    } catch (unsupported: Unsupported) {
        throw UnsupportedModule(unsupported.message.orEmpty())
    } catch (unsupported: UnsupportedShape) {
        throw UnsupportedModule(unsupported.message.orEmpty())
    }
}

internal data class ModuleMetadata(
    val calls: List<BuilderCall>,
    val trapSites: Map<Int, FunctionTrapSites>,
    val resources: DexResources,
    val limits: PoolLimits,
)

internal fun declarationCalls(
    plan: ModulePlan,
    declarations: ModuleDeclarations,
    resources: DexResources,
): List<BuilderCall> {
    return if (declarations.dataSegments.isNotEmpty()) {
        resources.resource(dataResource(plan.entryPath)) { stream -> DeclarationCallGenerator(declarations, stream) }
    } else {
        DeclarationCallGenerator(declarations, DiscardingOutputStream())
    }
}

private fun lowerFiles(
    plan: ModulePlan,
    layout: DexLayout,
    metadata: ModuleMetadata,
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
    val loweredModule = LoweredModule(lowered, lowering.chunkStarts, bound, lowering.emittedByteArrayBase)

    return finishModule(plan, codeDex, codeAndEntryFile, loweredModule, metadata, entryLowerer, installLowerer)
}

internal fun lowerBatch(
    lowering: ModuleLowering,
    batch: Int,
    file: DexFile,
    lowered: LoweredFunctions,
    functionLowerer: FunctionLowerer,
) {

    if (batch == 0) lowering.addSignatureInterfaces(file)
    val owner = lowering.plan.names.code(batch)
    val classBuilder = lowering.codeClassBuilder(file, owner)
    lowering.constants.begin(owner)

    for (function in lowering.batches[batch]) {
        val method = lowering.functionInvocation(function.index).method
        val reason = functionLowerer(lowering, function, classBuilder, method)
        lowered.record(function.index.index, reason)
    }

    lowering.constants.declare(classBuilder)
}

internal class LoweredModule(
    val functions: LoweredFunctions,
    val chunkStarts: Array<IntArray?>,
    val bound: BoundResources,
    val emittedByteArrayBase: Boolean,
)

internal fun finishModule(
    plan: ModulePlan,
    codeDex: List<ByteArray>,
    codeAndEntryFile: DexFile?,
    lowered: LoweredModule,
    metadata: ModuleMetadata,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val reasons = lowered.functions.reasons(plan.functions)
    if (reasons.isNotEmpty()) {
        codeAndEntryFile?.abort()
        throw UnsupportedFunctions(reasons)
    }
    return try {
        finishFiles(plan, codeDex, codeAndEntryFile, lowered, metadata, entryLowerer, installLowerer)
    } catch (failure: Throwable) {
        codeAndEntryFile?.abort()
        throw failure
    }
}

private fun finishFiles(
    plan: ModulePlan,
    codeDex: List<ByteArray>,
    codeAndEntryFile: DexFile?,
    lowered: LoweredModule,
    metadata: ModuleMetadata,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val entries = arrayOfNulls<EntryRef>(plan.functionCount)
    val entryClasses = LinkedHashSet<String>()
    val groups = entryGroups(plan)
    val dexter = Dexter()
    val dex = ArrayList(codeDex)
    val entryFiles = if (codeAndEntryFile != null) listOf(groups) else groups.chunkedBy(ENTRY_FUNCTIONS_PER_FILE)
    for (members in entryFiles) {
        val file = codeAndEntryFile ?: dexter.beginFile()
        for (group in members) {
            val descriptor = entryLowerer(file, group, plan.entryPath)
            entryClasses += descriptor
            group.members.forEachIndexed { id, member -> entries[member.index] = EntryRef(descriptor, id) }
        }
        if (codeAndEntryFile == null) dex += file.finish()
    }

    val ordered = LinkedHashMap<Int, EntryRef>()
    entries.forEachIndexed { function, entry -> if (entry != null) ordered[function] = entry }

    val files = MetadataFiles(dexter, metadata.limits, codeAndEntryFile)
    val generated = try {
        writeMetadata(plan, files, metadata, lowered, ordered, entryClasses, installLowerer)
    } catch (failure: Throwable) {
        files.abort()
        throw failure
    }
    dex += files.finish()

    if (plan.options.stacktraces) {
        val sites = trapSites(plan, metadata.trapSites, lowered.chunkStarts)
        metadata.resources.resource(trapSitesResource(plan.entryPath)) { stream -> writeTrapSites(stream, sites) }
    }

    return DexModule(
        dex = dex,
        moduleClass = binaryName(plan.names.module()),
        classes = (generated + plan.names.module()).map(::binaryName),
    )
}

private fun writeMetadata(
    plan: ModulePlan,
    files: MetadataFiles,
    metadata: ModuleMetadata,
    lowered: LoweredModule,
    ordered: Map<Int, EntryRef>,
    entryClasses: Set<String>,
    installLowerer: InstallLowerer,
): List<String> {

    val declareClasses = DeclarationsWriter(plan.names).write(files, metadata.calls)
    val structClasses = StructsWriter(plan.names, plan.structLanes).write(files)
    val installEntries = installLowerer(files, plan.entryPath, lowered.bound, ordered)
    val constantExpressions = ConstantExpressions(plan.module)
    val constants = plan.names.constants().takeIf { constantExpressions.needed }
    if (constants != null) {
        ConstantsWriter(plan.module, plan.storagePlans, constantExpressions).write(files, constants)
    }

    val codeClasses = plan.batches.indices.map(plan.names::code)
    val generated = codeClasses + plan.signatureClasses + entryClasses + installEntries + installClass(plan.entryPath) +
        declareClasses + structClasses + listOfNotNull(constants)
    val offset = if (lowered.emittedByteArrayBase) {
        checkNotNull(plan.options.byteArrayBaseOffset)
    } else {
        UNPINNED_OFFSET
    }
    ModuleClassWriter(plan, offset)
        .write(files, declareClasses, structClasses, generated.map(::binaryName), lowered.bound, installEntries, constants)

    return generated
}

private fun trapSites(
    plan: ModulePlan,
    sites: Map<Int, FunctionTrapSites>,
    chunkStarts: Array<IntArray?>,
): Map<Int, FunctionTrapSites> {
    return plan.functions.associate { function ->
        val index = function.index.index
        val functionSites = sites[index] ?: FunctionTrapSites(IntArray(0), IntArray(0))
        index to functionSites.withChunkStarts(chunkStarts[index] ?: IntArray(0))
    }
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

private fun entryGroups(plan: ModulePlan): List<EntryGroup> {

    val shapeGroups = entryShapeGroups(plan.functions, ENTRY_GROUP_SIZE) { function -> plan.functionSignature(function.type).entryShape }

    return shapeGroups.mapIndexed { number, shapeGroup ->
        EntryGroup(
            number = number,
            shape = shapeGroup.shape,
            signature = plan.signatureClass(plan.functionSignature(shapeGroup.members.first().type).key),
            members = shapeGroup.members.map { function -> LoweredFunction(function.index.index, plan.functionInvocation(function.index).method) },
            referenceDescriptor = plan.referenceDescriptor,
        )
    }
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
