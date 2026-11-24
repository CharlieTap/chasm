package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi

// Generated function methods are named FUNCTION_METHOD_PREFIX + index, and the chunks of a split function
// add CHUNK_METHOD_SEPARATOR + chunk; they live in classes named with CODE_CLASS_PREFIX or CHUNK_CLASS_PREFIX.
@InternalChasmApi
const val FUNCTION_METHOD_PREFIX = "f"

@InternalChasmApi
const val CHUNK_METHOD_SEPARATOR = "\$c"

@InternalChasmApi
const val CODE_CLASS_PREFIX = "Code"

@InternalChasmApi
const val CHUNK_CLASS_PREFIX = "Chunks"

// The managed engine's entry into compiled code as the shrinker rules name it, before any renaming.
@InternalChasmApi
const val MANAGED_ENTRY_CLASS = "io.github.charlietap.chasm.runtime.compiled.gc.ManagedEntry"

// The source file classes compiled with trap diagnostics name, which tells them apart from classes that share their names.
@InternalChasmApi
const val TRAP_DIAGNOSTICS_SOURCE_FILE = "module.wasm"
