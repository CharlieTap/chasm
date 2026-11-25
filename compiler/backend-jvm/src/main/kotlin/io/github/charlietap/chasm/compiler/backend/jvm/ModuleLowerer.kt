@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.compiled.format.writeTrapSites
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.UnsupportedFunctions
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.DeclarationCallGenerator
import io.github.charlietap.chasm.compiler.backend.analysis.declarations.DiscardingOutputStream
import io.github.charlietap.chasm.compiler.backend.jvm.abi.entriesClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationsLowerer
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites
import org.objectweb.asm.ClassTooLargeException
import org.objectweb.asm.tree.MethodNode
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal const val LOWERED_AHEAD = 4

internal const val LOWERED_INSTRUCTIONS = 1_000_000L

internal const val ENTRY_GROUP = 256

internal fun <T> completed(
    future: Future<T>,
): T = try {
    future.get()
} catch (failure: ExecutionException) {
    throw failure.cause ?: failure
}

typealias ModuleLowerer = (Module, CirModule, JvmOptions, JvmOutput, ModuleDeclarations, Map<Int, FunctionTrapSites>) -> JvmModule

fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: JvmOptions,
    output: JvmOutput,
    declarations: ModuleDeclarations,
): JvmModule = ModuleLowerer(module, code, options, output, declarations, emptyMap())

fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: JvmOptions,
    output: JvmOutput,
    declarations: ModuleDeclarations,
    trapSites: Map<Int, FunctionTrapSites>,
): JvmModule =
    ModuleLowerer(
        module = module,
        code = code,
        options = options,
        output = output,
        declarations = declarations,
        trapSites = trapSites,
        parallelism = Runtime.getRuntime().availableProcessors(),
        functionLowerer = ::FunctionLowerer,
        signatureLowerer = ::SignatureLowerer,
        entryLowerer = ::EntryLowerer,
        constantsLowerer = ::ConstantsLowerer,
        bindingLowerer = ::BindingLowerer,
        declarationCallGenerator = ::DeclarationCallGenerator,
        declarationsLowerer = ::DeclarationsLowerer,
        moduleClassLowerer = ::ModuleClassLowerer,
        structsLowerer = ::ModuleStructsLowerer,
    )

internal inline fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: JvmOptions,
    output: JvmOutput,
    declarations: ModuleDeclarations,
    trapSites: Map<Int, FunctionTrapSites> = emptyMap(),
    parallelism: Int,
    crossinline functionLowerer: FunctionLowerer,
    crossinline signatureLowerer: SignatureLowerer,
    crossinline entryLowerer: EntryLowerer,
    crossinline constantsLowerer: ConstantsLowerer,
    crossinline bindingLowerer: BindingLowerer,
    crossinline declarationCallGenerator: DeclarationCallGenerator,
    crossinline declarationsLowerer: DeclarationsLowerer,
    crossinline moduleClassLowerer: ModuleClassLowerer,
    noinline structsLowerer: ModuleStructsLowerer,
): JvmModule = try {
    val inventory = ClassInventory(output)
    val lowering = ModuleLowering(module, code, options)
    val entryPath = lowering.entryPath
    val reasons = LinkedHashMap<Int, String>()
    val chunkStarts = HashMap<Int, IntArray>()
    var entries: MutableList<InstalledEntry> = ArrayList()
    val pool = if (parallelism > 1) Executors.newFixedThreadPool(parallelism) else null
    try {
        val functions = lowering.batches.flatten()
        val pending = ArrayDeque<Future<FunctionResult>>()
        val writes = ArrayDeque<Pair<String, Future<ByteArray>>>()
        var next = 0
        var weight = 0L
        lowering.batches.forEachIndexed { batch, batchFunctions ->
            val owner = lowering.names.code(batch)
            val methods = ArrayList<MethodNode>()
            for (function in batchFunctions) {
                while (
                    next < functions.size &&
                    pending.size < maxOf(1, parallelism) * LOWERED_AHEAD &&
                    (pending.isEmpty() || weight + functions[next].instructions.size <= LOWERED_INSTRUCTIONS)
                ) {
                    val ahead = functions[next++]
                    val method = lowering.functionMethod(ahead.index)
                    weight += ahead.instructions.size
                    pending += pool?.submit(Callable { functionLowerer(lowering, ahead, method) })
                        ?: CompletableFuture.completedFuture(functionLowerer(lowering, ahead, method))
                }
                val result = completed(pending.removeFirst())
                weight -= function.instructions.size
                when (result) {
                    is FunctionResult.Rejected -> reasons[function.index.index] = result.reason
                    is FunctionResult.Lowered -> {
                        lowering.layout.resolve(result.methods.map(GeneratedMethod::node), result.resources, lowering.bindingType)
                        if (options.stacktraces && result.methods.size > 1) {
                            chunkStarts[function.index.index] = result.methods.mapNotNull { generated ->
                                generated.chunkStart.takeIf { start -> start >= 0 }
                            }.toIntArray()
                        }
                        result.methods.filter { generated -> generated.owner == null }.forEach { generated -> methods += generated.node }
                        result.methods.filter { generated -> generated.owner != null }.groupBy({ it.owner }, { it.node }).forEach { (chunks, nodes) ->
                            val name = checkNotNull(chunks)
                            writes += name to (
                                pool?.submit(Callable { lowering.methodsClass(name, public = false, nodes) })
                                    ?: CompletableFuture.completedFuture(lowering.methodsClass(name, public = false, nodes))
                            )
                        }
                    }
                }
            }
            writes += owner to (
                pool?.submit(Callable { lowering.methodsClass(owner, public = true, methods) })
                    ?: CompletableFuture.completedFuture(lowering.methodsClass(owner, public = true, methods))
            )
            while (writes.isNotEmpty() && writes.first().second.isDone) {
                val (name, bytes) = writes.removeFirst()
                inventory.classFile(internalName(name), completed(bytes))
            }
        }
        while (writes.isNotEmpty()) {
            val (name, bytes) = writes.removeFirst()
            inventory.classFile(internalName(name), completed(bytes))
        }
        if (reasons.isNotEmpty()) throw UnsupportedFunctions(reasons)
        lowering.typedMethods.forEach { (signature, typed) ->
            inventory.classFile(internalName(signature), signatureLowerer(signature, typed))
        }
        val groups = code.functions
            .groupBy { function -> lowering.declarations.functionType(function.index) }
            .values.flatMap { functions -> functions.chunked(ENTRY_GROUP) }
            .sortedBy { group -> group.first().index.index }
        entries = ArrayList()
        val entryClasses = groups.mapIndexed { number, group ->
            val first = group.first()
            val entry = entriesClassDescriptor(entryPath, number)
            group.forEachIndexed { id, function -> entries += InstalledEntry(function.index.index, entry, if (group.size == 1) -1 else id) }
            val type = lowering.declarations.functionType(first.index)
            val signature = checkNotNull(lowering.signature(type))
            val methods = group.map { function -> lowering.functionMethod(function.index) }
            entry to (
                pool?.submit(Callable { entryLowerer(lowering, entry, methods, type, signature) })
                    ?: CompletableFuture.completedFuture(entryLowerer(lowering, entry, methods, type, signature))
            )
        }
        entryClasses.forEach { (entry, bytes) -> inventory.classFile(internalName(entry), completed(bytes)) }
        entries.sortBy { it.function }
    } finally {
        pool?.shutdownNow()
    }
    constantsLowerer(lowering, module, inventory)
    bindingLowerer(lowering, inventory)
    val calls = if (declarations.dataSegments.isNotEmpty()) {
        inventory.resource(dataResource(entryPath)) { stream -> declarationCallGenerator(declarations, stream) }
    } else {
        declarationCallGenerator(declarations, DiscardingOutputStream())
    }
    val declarationClasses = declarationsLowerer(calls, lowering.names, inventory)
    if (options.stacktraces) {
        val sites = code.functions.associate { function ->
            val index = function.index.index
            val sites = trapSites[index] ?: FunctionTrapSites(IntArray(0), IntArray(0))
            index to sites.withChunkStarts(chunkStarts[index] ?: IntArray(0))
        }
        inventory.resource(trapSitesResource(entryPath)) { stream -> writeTrapSites(stream, sites) }
    }
    val moduleDescriptor = lowering.names.module()
    moduleClassLowerer(lowering, moduleDescriptor, declarationClasses, entries, structsLowerer, inventory)
    JvmModule(internalName(moduleDescriptor).replace('/', '.'))
} catch (tooLarge: ClassTooLargeException) {
    throw ClassTooLarge(
        "generated class ${tooLarge.className} needs ${tooLarge.constantPoolCount} constants, more than the JVM's 65535",
        tooLarge,
    )
}
