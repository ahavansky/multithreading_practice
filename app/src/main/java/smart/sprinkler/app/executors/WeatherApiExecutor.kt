package smart.sprinkler.app.executors

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class WeatherApiExecutor private constructor(uiLooper: Looper) {
    private val mUiHandler = Handler(uiLooper)
    private val mThreadPoolExecutor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )

    private val mTasks = ConcurrentHashMap<Int, Future<*>>()
    private val mCache = mutableSetOf<Response<*>>()
    private val mTaskKey = AtomicInteger(0)

    inline fun <reified T> execute(
        noinline apiCall: () -> Call<T>,
        noinline updateUiCall: (T) -> Unit,
        noinline errorCall: (String) -> Unit,
        useCache: Boolean = true
    ) = execute(apiCall, updateUiCall, errorCall, useCache, T::class.java)

    fun <T> execute(
        apiCall: () -> Call<T>,
        updateUiCall: (T) -> Unit,
        errorCall: (String) -> Unit,
        useCache: Boolean = true,
        klass: Class<T>
    ) {
        val key = mTaskKey.getAndIncrement()
        mTasks[key] = mThreadPoolExecutor.submit {
            try {
                var resultFromCache: Response<T>? = null
                if (useCache) {
                    @Suppress("UNCHECKED_CAST")
                    resultFromCache = mCache
                        .filter { it.body() != null }
                        .find { it.body()!!::class.java == klass } as? Response<T>
                }

                val result = resultFromCache ?: apiCall.invoke().execute()
                val resultBody = result.body()
                if (result.isSuccessful && resultBody != null) {
                    mCache.add(result)
                    postUiHandler { updateUiCall.invoke(resultBody) }
                } else {
                    val errorBody = result.errorBody()
                    if (errorBody != null) {
                        throw RetrofitException(result.toString())
                    } else {
                        throw RetrofitException("Unknown error")
                    }
                }
            } catch (e: Exception) {
                val message = e.message ?: "Unknown error"
                postUiHandler { errorCall.invoke(message) }
            } finally {
                mTasks.remove(key)
            }
        }
    }

    fun pause() {
        mTasks.filter { it.value.isCancelled.not() }.values.forEach { it.cancel(true) }
        mTasks.clear()
    }

    private fun shutdown() {
        pause()
        mThreadPoolExecutor.shutdown()
    }

    private fun postUiHandler(runnable: Runnable) {
        mUiHandler.post {
            if (Thread.currentThread().isInterrupted.not()) {
                runnable.run()
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherApiExecutor? = null

        fun getInstance(looper: Looper) =
            synchronized(this) {
                INSTANCE ?: WeatherApiExecutor(looper).also { INSTANCE = it }
            }

        fun shutdown() {
            synchronized(this) {
                INSTANCE?.shutdown()
                INSTANCE = null
            }
        }

        private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = NUMBER_OF_CORES * 2
        private val MAXIMUM_POOL_SIZE = NUMBER_OF_CORES * 2
        private val KEEP_ALIVE_TIME = NUMBER_OF_CORES * 2L
    }
}