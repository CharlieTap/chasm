package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.parallel.ParallelTaskExecutor

internal fun interpreterEngine(
    storeRuntime: StoreRuntime,
    taskExecutor: ParallelTaskExecutor,
    trapSources: TrapSources,
): InterpreterEngine {

    val heap = storeRuntime.liveStore().heap
    val binding = EngineBinding()
    val heldReferences = HeldReferenceTable(binding)
    val codec = InterpreterReferenceCodec(binding, storeRuntime, heldReferences, heap)
    val handles = InterpreterHandles(binding, storeRuntime, codec)
    val allocator = EmbeddingAllocator(storeRuntime)
    val resources = InterpreterHostResources(binding, handles)

    val engine = InterpreterEngine(
        storeRuntime = storeRuntime,
        taskExecutor = taskExecutor,
        trapSources = trapSources,
        binding = binding,
        heap = heap,
        heldReferences = heldReferences,
        codec = codec,
        handles = handles,
        values = BoxedValues(),
        hostCalls = InterpreterHostCallAdapters(codec, ReferenceLanes(), resources),
        globals = InterpreterGlobals(handles, codec, heap),
        tables = InterpreterTables(handles, codec, heap),
        structs = InterpreterStructs(codec, heldReferences, allocator, heap),
        arrays = InterpreterArrays(codec, heldReferences, allocator, heap),
        exceptions = InterpreterExceptions(handles, codec, heldReferences, allocator, heap),
    )
    binding.bind(engine)
    heap.heldRoots = heldReferences

    return engine
}
