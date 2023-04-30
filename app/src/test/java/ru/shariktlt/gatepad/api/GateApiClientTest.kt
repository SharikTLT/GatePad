package ru.shariktlt.gatepad.api

import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import ru.shariktlt.gatepad.IStoreManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class GateApiClientTest {

    @Test
    fun signInTest() {
        val jwt = AtomicReference<String>("")
        val storage = mock<IStoreManager> {
            on { getJWT() } doAnswer({
                jwt.get()!!
            })
            on { setJWT(anyOrNull()) } doAnswer ({
                jwt.set(it.getArgument<String>(0))
            })
        }

        val client = GateApiClient("261c40ee-418f-4fe6-a5a2-386194f2b679", storage)
        var completed = CompletableFuture<Void>()
        client.signIn(
            System.getenv("PHONE"),
            System.getenv("PASS"),
            {
                completed.complete(null);
            }, {
                completed.complete(null)
            })
        completed.get(10, TimeUnit.SECONDS)
        assertTrue(completed.isDone)
        assertNotEquals("", jwt.get())

        completed = CompletableFuture<Void>()

        client.getRelays({
            assertNotNull(it)
            completed.complete(null)
        },
            {

            }
        )
        completed.get(10, TimeUnit.SECONDS)
        assertTrue(completed.isDone)

//        completed = CompletableFuture<Void>()
//
//        client.unlock(40625, {
//            assertNotNull(it)
//            completed.complete(null)
//        },
//            {
//
//            }
//        )
//        completed.get(10, TimeUnit.SECONDS)
//        assertTrue(completed.isDone)
    }
}