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
) : CoroutineScope {

    val executorService = Executors.newWorkStealingPool()

    override val coroutineContext: CoroutineContext = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            executorService.execute(block)
        }
    }

    val stream: Observable<TokenDetail> by lazy {
        requestNextToken(0).replay(1).autoConnect()
    }

    fun current() = stream.take(1).singleOrError()!!

    private fun requestNextToken(idx: Long): Observable<TokenDetail> {
        return Observable.just(idx).map {
            Thread.sleep(5000)
            "idx + $it"
        }.subscribeOn(Schedulers.io())
    }

    @Get("/tryout/")
    fun tryout() = theBean.thePojo
}