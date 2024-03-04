@file:Suppress("UNUSED_PARAMETER")

package io.github.charlietap.chasm.ext
//
// import com.github.michaelbull.result.Result
// import com.github.michaelbull.result.binding
// import io.github.charlietap.chasm.Chasm
// import io.github.charlietap.chasm.ChasmResult
// import io.github.charlietap.chasm.SourceReader
// import io.github.charlietap.chasm.ast.module.Module
// import io.github.charlietap.chasm.error.ChasmError
// import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
// import io.github.charlietap.chasm.executor.runtime.import.Import
// import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
// import io.github.charlietap.chasm.executor.runtime.store.Store
// import io.github.charlietap.chasm.executor.instantiation.ImportMatcher
// import io.github.charlietap.chasm.executor.instantiation.ImportMatcherImpl
// import io.github.charlietap.chasm.executor.instantiation.ModuleInstantiator
// import io.github.charlietap.chasm.executor.instantiation.ModuleInstantiatorImpl
//
// fun Chasm.decode(
//    moduleBytes: ByteArray,
// ): ChasmResult<Module, ChasmError.DecodeError> = TODO()
//
// fun Chasm.decode(
//    reader: SourceReader,
// ): ChasmResult<Module, ChasmError.DecodeError> = TODO()
//
// fun Chasm.store() = Store.Empty.copy()
//
// fun Chasm.instantiate(
//    reader: SourceReader,
//    imports: List<Import>,
//    store: Store = store(),
// ): ChasmResult<ModuleInstance, ChasmError> = TODO()
//
// fun Chasm.instantiate(
//    module: Module,
//    imports: List<Import>,
//    store: Store = store(),
// ): ChasmResult<ModuleInstance, ChasmError> =
//    instantiate(
//        store = store,
//        module = module,
//        imports = imports,
//        importMatcher = ::ImportMatcherImpl,
//        moduleInstantiator = ::ModuleInstantiatorImpl,
//    ).toChasmResult()
//
// internal fun Chasm.instantiate(
//    module: Module,
//    imports: List<Import>,
//    store: Store,
//    importMatcher: ImportMatcher,
//    moduleInstantiator: ModuleInstantiator,
// ): Result<ModuleInstance, InstantiationError> = binding {
//    val externs = importMatcher(module, imports).bind()
//    moduleInstantiator(store, module, externs).bind()
// }
