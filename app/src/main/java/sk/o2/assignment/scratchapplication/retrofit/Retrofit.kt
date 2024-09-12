package sk.o2.assignment.scratchapplication.retrofit

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstance {
    private const val BASE_URL = "https://api.o2.sk"

    fun getInstance(): Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

interface ApiInterface {
    @GET("/version")
    suspend fun getVersion(@Query("code") code: String): Response<VersionData>
}

data class VersionData(
    val ios: String,
    val iosTM: String,
    val iosRA: String,
    val iosRA2: String,
    val android: String,
    val androidTM: String,
    val androidRA: String
)