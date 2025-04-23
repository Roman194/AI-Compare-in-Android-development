 // 6.	ArtistsApiService.kt
interface ArtistsApiService {{//GigaChat prompt 3 1st attempt
    
    @GET("similar")
    fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") mediaType: String
    ): Call<Either<ErrorResponse, MoshiResponse>>

    companion object {
        const val MEDIA_TYPE_MUSIC = "music"
    }
}

// ClientBuilder.kt
object ArtistApi {
    private lateinit var artistsRetrofitService: ArtistsApiService

    init {
        initializeRetrofitService()
    }

    fun getService(): ArtistsApiService = artistsRetrofitService

    private fun initializeRetrofitService() {
        val moshi = Moshi.Builder().build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ParseConstants.loggingInterceptor())
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory.create())
            .build()

        artistsRetrofitService = retrofit.create(ArtistsApiService::class.java)
    }
}

object ParseConstants {
    const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY

    companion object {
        fun loggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            return interceptor
        }
    }
}


//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name.", "")
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        val formattedName = convertArtistNameToQueryFormat(originalArtistName)

        val service = ArtistApi.getService()
        val call = service.getSimilarArtists(
            apiKey = ParseConstants.API_KEY,
            artistName = formattedName,
            mediaType = ArtistsApiService.MEDIA_TYPE_MUSIC
        )

        call.enqueue(object : Callback<Either<ErrorResponse, MoshiResponse>> {
            override fun onResponse(call: Call<Either<ErrorResponse, MoshiResponse>>, response: Response<Either<ErrorResponse, MoshiResponse>>) {
                handleAPIResponse(response, originalArtistName)
            }

            override fun onFailure(call: Call<Either<ErrorResponse, MoshiResponse>>, throwable: Throwable) {
                findArtistsUiState = FindArtistUiState.Error("Network failure: ${throwable.localizedMessage}", originalArtistName)
            }
        })
    }

    private fun handleAPIResponse(response: Response<Either<ErrorResponse, MoshiResponse>>, originalArtistName: String) {
        val eitherResponse = response.body()
        if (eitherResponse == null || !response.isSuccessful) {
            findArtistsUiState = FindArtistUiState.Error("Server error occurred.", originalArtistName)
            return
        }

        when (val result = eitherResponse) {
            is Either.Success -> {
                val artistList = result.value.similar.results.map { it.toUI() }
                findArtistsUiState = FindArtistUiState.Success(artistList, originalArtistName)
            }
            is Either.Failure -> {
                val errorMsg = result.value.toStringType()
                findArtistsUiState = FindArtistUiState.Error(errorMsg, originalArtistName)
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}