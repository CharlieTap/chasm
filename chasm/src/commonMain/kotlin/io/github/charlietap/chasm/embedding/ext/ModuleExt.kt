package io.github.charlietap.chasm.embedding.ext

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.NameSubsection

internal inline fun <reified T : NameSubsection> Module.nameSubsection(): T? {
    val nameData = customs.filterIsInstance<NameData>().firstOrNull()
    return nameData?.subsections?.filterIsInstance<T>()?.firstOrNull()
}
