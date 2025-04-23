/**
* Необходимо ещё немного доработать решение. Верни пожалуйста в ArtistsApiService.kt в функцию getSimilarArtists query k и передай в него напрямую значение 
* ключа API из ParseConstants.
*/

//1. ArtistsApiService.kt
interface ArtistApiService {//Qwen prompt 3 3rd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY, // Передаем API ключ напрямую
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Настройка для работы с Kotlin объектами
            .build()

        Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()
    }

    val artistsRetrofitService: ArtistApiService by lazy {
        retrofit.create(ArtistApiService::class.java)
    }
}


