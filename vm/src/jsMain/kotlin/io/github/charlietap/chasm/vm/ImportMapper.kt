package io.github.charlietap.chasm.vm

internal object ImportMapper {

    fun build(imports: List<Import>): dynamic {
        val root: dynamic = newObj()

        for (import in imports) {
            val moduleName = import.moduleName
            val entityName = import.entityName

            val existing: dynamic = root[moduleName]
            val moduleObj: dynamic = if (existing == null || existing === UNDEFINED) newObj() else existing
            root[moduleName] = moduleObj

            val value: dynamic = when (val address = import.address) {
                is ExternalAddress.Function -> address.address.fn
                is ExternalAddress.Global -> address.address
                is ExternalAddress.Memory -> address.address
                is ExternalAddress.Table -> address.address
                is ExternalAddress.Tag -> address.address
            }

            moduleObj[entityName] = value
        }

        return root
    }
}
