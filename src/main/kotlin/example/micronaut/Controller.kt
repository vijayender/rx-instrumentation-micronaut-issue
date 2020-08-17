package example.micronaut

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

// bean with cyclic dependency fails during test

data class ThePojo(
        val x: Int
)

@Client("http://localhost:8081")
interface TheClient {
    @Get("/")
    fun getSome(): CompletableFuture<ThePojo>
}

@Singleton
class TheBean(
        //objectMapper: ObjectMapper,
        private val theClient: TheClient
) {
    var thePojo: ThePojo = ThePojo(-10)
    @PostConstruct
    fun init() {
        thePojo = theClient.getSome().get()
    }
}

typealias TokenDetail = String
@Controller
class Controller(
        private val executorService2: ExecutorService,
        private val theBean: TheBean
)  {

    @Get("/tryout/")
    fun tryout() = theBean.thePojo
}