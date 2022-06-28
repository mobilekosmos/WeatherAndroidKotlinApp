package com.mobilekosmos.android.weather.data.network

import com.mobilekosmos.android.weather.MyApplication
import com.mobilekosmos.android.weather.data.model.WeatherForecastEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://62b9bbd641bf319d22841c93.mockapi.io/api/v1/forecast"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Create a cache object
const val CACHE_SIZE = 1 * 1024 * 1024 // 1 MB
val httpCacheDirectory = File(MyApplication.instance.cacheDir, "http-cache")
val cache = Cache(httpCacheDirectory, CACHE_SIZE.toLong())

// create a network cache interceptor, setting the max age to 1 minute
private val networkOfflineCacheInterceptor = Interceptor { chain ->
    var request = chain.request()

    val cacheControl: CacheControl = if (Utils.isNetworkAvailable()) {
        CacheControl.Builder()
            .maxAge(1, TimeUnit.MINUTES)
            .build()
    } else {
        CacheControl.Builder()
            .onlyIfCached()
            .maxStale(1, TimeUnit.DAYS)
            .build()
    }

    request = request.newBuilder().cacheControl(cacheControl).build()
    chain.proceed(request)
}

private val networkCacheInterceptor = Interceptor { chain ->
    val request = chain.request()
    val cacheControl: CacheControl = if (Utils.isNetworkAvailable()) {
        CacheControl.Builder()
            .maxAge(1, TimeUnit.MINUTES)
            .build()
    } else {
        CacheControl.Builder()
            .onlyIfCached()
            .maxStale(1, TimeUnit.DAYS)
            .build()
    }
    val response = chain.proceed(request)
    // Servers may set "no-cache" in the response, so we remove this.
    response.newBuilder()
        .removeHeader("Pragma")
        .removeHeader("Cache-Control")
        .header("Cache-Control", cacheControl.toString())
        .build()
}

// Create the logging interceptor. Search for tag "okhttp" in Logcat.
private val networkLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

// Create the httpClient, configure it
// with cache, network cache interceptor and logging interceptor
// TODO: don't use loggingInterceptor in release build.
private val httpClient = OkHttpClient.Builder()
    .cache(cache)
    .addNetworkInterceptor(networkCacheInterceptor)
    .addInterceptor(networkOfflineCacheInterceptor)
    .addInterceptor(networkLoggingInterceptor)
    .build()

// Create the Retrofit with the httpClient
private val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient)
    .build()

/**
 * A public interface that exposes the [getWeatherForecasts] method
 */
interface WeatherForecastsApiService {
    @GET(BASE_URL)
    suspend fun getWeatherForecasts(): List<WeatherForecastEntity>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service.
 */
object WeatherForecastApi {
    val RETROFIT_SERVICE: WeatherForecastsApiService by lazy {
        retrofit.create(WeatherForecastsApiService::class.java)
    }
}
