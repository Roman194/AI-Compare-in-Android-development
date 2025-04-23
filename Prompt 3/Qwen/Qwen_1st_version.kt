//1. ArtistsApiService.kt

interface ArtistApiService { //Qwen prompt 3 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

//2. ClientBuilder.kt

object ArtistApi {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val moshi = Moshi.Builder().build()

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

class ParseConstants {
    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
    }
}

//FindArtistsViewModel.kt

class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        // Initial state change to Loading
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        // Check if the artist name is empty
        if (originalArtistName.trim().isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        // Convert artist name to query format
        val queryArtistName = convertArtistNameToQueryFormat(originalArtistName)

        // Launch a coroutine to perform the network request
        viewModelScope.launch {
            try {
                val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                    apiKey = ParseConstants.API_KEY,
                    artistName = queryArtistName
                )

                when (response) {
                    is Either.Fail -> {
                        // Handle error response
                        val error = response.value
                        findArtistsUiState = FindArtistUiState.Error(
                            errorMessage = error.toStringType(),
                            originalArtist = originalArtistName
                        )
                    }
                    is Either.Success -> {
                        // Deserialize the response and update UI state
                        val similarArtists = response.value.similar.results.map { it.toUI() }
                        findArtistsUiState = FindArtistUiState.Success(
                            artistUIValues = similarArtists,
                            originalArtist = originalArtistName
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                val error = ErrorResponse.getValue(700)
                findArtistsUiState = FindArtistUiState.Error(
                    errorMessage = error.toStringType(),
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
