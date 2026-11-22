package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
fun interpreterEngine(
    storeRuntime: StoreRuntime,
    installer: ModuleInstaller,
): InterpreterEngine {

    val heap = storeRuntime.runtime().heap
    val binding = EngineBinding()
    val callConfig = HostCallConfig()
    val heldReferences = HeldReferenceTable(binding)
    val codec = InterpreterReferenceCodec(binding, storeRuntime, heldReferences, heap)
    val handles = InterpreterHandles(binding, storeRuntime, codec)
    val allocator = EmbeddingAllocator(storeRuntime, callConfig)
    val resources = InterpreterHostResources(binding, handles)

    val engine = InterpreterEngine(
        storeRuntime = storeRuntime,
        installer = installer,
        binding = binding,
        heap = heap,
        heldReferences = heldReferences,
        codec = codec,
        handles = handles,
        values = BoxedValues(),
        hostCalls = InterpreterHostCallAdapters(codec, ReferenceLanes(), resources, callConfig),
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
