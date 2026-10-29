package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.jvm.abi.codeClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.dataResource
import io.github.charlietap.chasm.compiler.backend.jvm.abi.entriesClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.moduleClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationCallGenerator
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationsLowerer
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import org.objectweb.asm.ClassTooLargeException
import org.objectweb.asm.tree.MethodNode
import java.io.OutputStream
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

typealias ModuleLowerer = (Module, CirModule, JvmOptions, JvmOutput, ModuleDeclarations) -> JvmModule

fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: JvmOptions,
    output: JvmOutput,
    declarations: ModuleDeclarations,
): JvmModule =
    ModuleLowerer(
        module = module,
        code = code,
        options = options,
        output = output,
        declarations = declarations,
        parallelism = Runtime.getRuntime().availableProcessors(),
        functionLowerer = ::FunctionLowerer,
        signatureLowerer = ::SignatureLowerer,
        entryLowerer = ::EntryLowerer,
        bindingLowerer = ::BindingLowerer,
        declarationCallGenerator = ::DeclarationCallGenerator,
        declarationsLowerer = ::DeclarationsLowerer,
        moduleClassLowerer = ::ModuleClassLowerer,
    )

internal inline fun ModuleLowerer(
    module: Module,
    code: CirModule,
    options: JvmOptions,
    output: JvmOutput,
    declarations: ModuleDeclarations,
    parallelism: Int,
    crossinline functionLowerer: FunctionLowerer,
    crossinline signatureLowerer: SignatureLowerer,
    crossinline entryLowerer: EntryLowerer,
    crossinline bindingLowerer: BindingLowerer,
    crossinline declarationCallGenerator: DeclarationCallGenerator,
    crossinline declarationsLowerer: DeclarationsLowerer,
    crossinline moduleClassLowerer: ModuleClassLowerer,
): JvmModule = try {
    val lowering = ModuleLowering(module, code, options)
    val packagePath = lowering.packagePath
    val reasons = LinkedHashMap<Int, String>()
    var entries: MutableList<InstalledEntry> = ArrayList()
    val pool = if (parallelism > 1) Executors.newFixedThreadPool(parallelism) else null
    try {
        val functions = lowering.batches.flatten()
        val pending = ArrayDeque<Future<FunctionResult>>()
        val writes = ArrayDeque<Pair<String, Future<ByteArray>>>()
        var next = 0
        var weight = 0L
        lowering.batches.forEachIndexed { batch, batchFunctions ->
            val owner = codeClassDescriptor(packagePath, batch)
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
                output.classFile(internalName(name), completed(bytes))
            }
        }
        while (writes.isNotEmpty()) {
            val (name, bytes) = writes.removeFirst()
            output.classFile(internalName(name), completed(bytes))
        }
        if (reasons.isNotEmpty()) throw UnsupportedFunctions(reasons)
        lowering.typedMethods.forEach { (signature, typed) -> output.classFile(internalName(signature), signatureLowerer(signature, typed)) }
        val groups = code.functions
            .groupBy { function -> lowering.declarations.functionType(function.index) }
            .values.flatMap { functions -> functions.chunked(ENTRY_GROUP) }
            .sortedBy { group -> group.first().index.index }
        entries = ArrayList()
        val entryClasses = groups.mapIndexed { number, group ->
            val first = group.first()
            val entry = entriesClassDescriptor(packagePath, number)
            group.forEachIndexed { id, function -> entries += InstalledEntry(function.index.index, entry, if (group.size == 1) -1 else id) }
            val type = lowering.declarations.functionType(first.index)
            val signature = checkNotNull(lowering.signature(type))
            val methods = group.map { function -> lowering.functionMethod(function.index) }
            entry to (
                pool?.submit(Callable { entryLowerer(lowering, entry, methods, type, signature) })
                    ?: CompletableFuture.completedFuture(entryLowerer(lowering, entry, methods, type, signature))
            )
        }
        entryClasses.forEach { (entry, bytes) -> output.classFile(internalName(entry), completed(bytes)) }
        entries.sortBy { it.function }
    } finally {
        pool?.shutdownNow()
    }
    bindingLowerer(lowering, output)
    val dataResourcePath = dataResource(packagePath).takeIf { declarations.dataSegments.isNotEmpty() }
    val calls = if (dataResourcePath != null) {
        output.resource(dataResourcePath) { stream -> declarationCallGenerator(declarations, stream) }
    } else {
        declarationCallGenerator(declarations, OutputStream.nullOutputStream())
    }
    val declarationClasses = declarationsLowerer(calls, packagePath, output)
    val moduleDescriptor = moduleClassDescriptor(packagePath)
    moduleClassLowerer(lowering, moduleDescriptor, declarationClasses, dataResourcePath, entries, output)
    JvmModule(internalName(moduleDescriptor).replace('/', '.'), dataResourcePath)
} catch (tooLarge: ClassTooLargeException) {
    throw ClassTooLarge(
        "generated class ${tooLarge.className} needs ${tooLarge.constantPoolCount} constants, more than the JVM's 65535",
        tooLarge,
    )
}
