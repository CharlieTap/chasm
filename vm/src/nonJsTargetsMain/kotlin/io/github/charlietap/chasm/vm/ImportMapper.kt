package io.github.charlietap.chasm.vm

import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Import as ChasmImport

internal object ImportMapper {

    fun from(import: Import): ChasmImport {
        val value: Importable = when (val address = import.address) {
            is ExternalAddress.Function -> address.address
            is ExternalAddress.Global -> address.address
            is ExternalAddress.Memory -> address.address
            is ExternalAddress.Table -> address.address
            is ExternalAddress.Tag -> address.address
        }
        return ChasmImport(
            moduleName = import.moduleName,
            entityName = import.entityName,
            value = value,
        )
    }
}
