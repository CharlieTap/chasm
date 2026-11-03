package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.dex.abi.codeClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.functionMethod
import io.github.charlietap.chasm.compiler.backend.dex.abi.installClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
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

internal inline fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    crossinline functionLowerer: FunctionLowerer,
    crossinline entryLowerer: EntryLowerer,
    crossinline installLowerer: InstallLowerer,
): DexModule = try {
    lowerModule(module, code, options, functionLowerer, entryLowerer, installLowerer)
} catch (tooLarge: DexterException) {
    throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
}

internal inline fun lowerModule(
    module: Module,
    code: CirModule,
    options: DexOptions,
    crossinline functionLowerer: FunctionLowerer,
    crossinline entryLowerer: EntryLowerer,
    crossinline installLowerer: InstallLowerer,
): DexModule {
    val lowering = ModuleLowering(module, code, options)
    val dexter = Dexter()
    val reasons = LinkedHashMap<Int, String>()
    val compiled = LinkedHashMap<FunctionType, MutableList<LoweredFunction>>()
    val dex = lowering.batches.mapIndexed { batch, functions ->
        dexFile(dexter) { file ->
            if (batch == 0) lowering.addSignatureInterfaces(file)
            val owner = codeClass(lowering.packagePath, batch)
            val classBuilder = lowering.codeClassBuilder(file, owner)
            for (function in functions) {
                val index = function.index.index
                val type = lowering.declarations.functionType(function.type)
                val method = functionMethod(owner, index, type).method
                val reason = functionLowerer(lowering, function, classBuilder, method)
                if (reason == null) {
                    compiled.getOrPut(type, ::ArrayList) += LoweredFunction(index, method)
                } else {
                    reasons[index] = reason
                }
            }
        }
    }
    if (reasons.isNotEmpty()) throw UnsupportedFunctions(reasons)
    val groups = entryGroups(compiled, lowering)
    val entries = HashMap<Int, EntryRef>()
    val entryDex = groups.chunkedBy(ENTRY_FUNCTIONS_PER_FILE).map { files ->
        dexFile(dexter) { file ->
            for (group in files) {
                val descriptor = entryLowerer(file, group, lowering.packagePath)
                group.members.forEachIndexed { id, member -> entries[member.index] = EntryRef(descriptor, id) }
            }
        }
    }
    val ordered = LinkedHashMap<Int, EntryRef>()
    entries.keys.sorted().forEach { function -> ordered[function] = entries.getValue(function) }
    val installer = installClass(lowering.packagePath)
    val install = installLowerer(dexter, installer, lowering.layout, ordered)
    return DexModule(dex + entryDex + install, binaryName(installer), lowering.inlineMemory != null, lowering.chunkStarts)
}

internal class EntryRef(
    val descriptor: String,
    val id: Int,
)

private fun entryGroups(
    compiled: Map<FunctionType, List<LoweredFunction>>,
    lowering: ModuleLowering,
): List<EntryGroup> {
    val groups = ArrayList<EntryGroup>()
    for ((type, functions) in compiled) {
        val signature = lowering.signatureClass(type)
        for (members in functions.chunked(ENTRY_GROUP_SIZE)) {
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
