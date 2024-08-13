package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.transform.BidirectionalMapper
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

fun exports(
    instance: Instance,
): List<Export> = exports(
    instance = instance,
    importableMapper = ImportableMapper,
)

internal fun exports(
    instance: Instance,
    importableMapper: BidirectionalMapper<Importable, ExternalValue>,
): List<Export> = instance.instance.exports.map { exportInstance ->
    Export(
        exportInstance.name.name,
        importableMapper.bimap(exportInstance.value),
    )
}
