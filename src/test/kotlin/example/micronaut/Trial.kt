package example.micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Test

class Trial{
    val embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)

    @Test
    fun tryOut() {
        println("Hello world")
        embeddedServer.applicationContext.getBean(TheBean::class.java)
    }
}