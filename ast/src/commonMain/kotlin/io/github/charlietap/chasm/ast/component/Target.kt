package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Index.ComponentFunctionIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentInstanceIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentModuleInstanceIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentValueIndex
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex as ModuleGlobalIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex as ModuleMemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex as ModuleTableIndex
import io.github.charlietap.chasm.ast.module.Index.TagIndex as ModuleTagIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex

@InternalChasmApi
sealed interface ExportTarget {

    data class Module(val index: ComponentModuleIndex) : ExportTarget

    data class Function(val index: ComponentFunctionIndex) : ExportTarget

    data class Value(val index: ComponentValueIndex) : ExportTarget

    data class Type(val index: ComponentTypeIndex) : ExportTarget

    data class Component(val index: ComponentIndex) : ExportTarget

    data class Instance(val index: ComponentInstanceIndex) : ExportTarget
}

@InternalChasmApi
sealed interface CoreExportTarget {

    data class Function(val index: ModuleFunctionIndex) : CoreExportTarget

    data class Table(val index: ModuleTableIndex) : CoreExportTarget

    data class Memory(val index: ModuleMemoryIndex) : CoreExportTarget

    data class Global(val index: ModuleGlobalIndex) : CoreExportTarget

    data class Tag(val index: ModuleTagIndex) : CoreExportTarget

    data class Type(val index: ModuleTypeIndex) : CoreExportTarget

    data class Module(val index: ComponentModuleIndex) : CoreExportTarget

    data class Instance(val index: ComponentModuleInstanceIndex) : CoreExportTarget
}
