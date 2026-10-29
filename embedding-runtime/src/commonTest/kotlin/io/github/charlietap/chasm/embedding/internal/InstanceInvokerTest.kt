package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.fixture.instanceInvoker
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.prepareFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class InstanceInvokerTest {

    @Test
    fun `invoke and prepared functions run through the invoker the instance was created with`() {

        val function = hostFunctionInstance()
        val store = publicStore(store(functions = mutableListOf(function)))
        val module = moduleInstance(
            exports = mutableListOf(exportInstance(nameValue("run"), functionExternalValue(functionAddress(0)))),
        )
        val calls = ArrayList<String>()
        val invoker = instanceInvoker(
            byAddress = { _, _, instance, address, _ ->
                assertSame(module, instance)
                assertEquals(functionAddress(0), address)
                calls += "address"
                Ok(listOf(i32(1)))
            },
            byFunction = { _, _, instance, invoked, _ ->
                assertSame(module, instance)
                assertSame(function, invoked)
                calls += "function"
                Ok(listOf(i32(2)))
            },
        )

        val instance: Instance = instantiate(store, emptyList(), RuntimeConfig(), invoker, { null }) { _, _, _ -> Ok(module) }
            .expect("instantiates")
        val prepared = prepareFunction(store, instance, "run").expect("prepares")

        assertEquals(ChasmResult.Success(listOf(i32(1))), invoke(store, instance, "run"))
        assertEquals(ChasmResult.Success(listOf(i32(2))), prepared())
        assertEquals(listOf("address", "function"), calls)
    }
}
