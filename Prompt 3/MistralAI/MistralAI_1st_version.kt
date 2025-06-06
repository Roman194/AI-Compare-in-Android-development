//ArtistsApiService.kt
interface ArtistsApiService { //Mistral AI prompt 3 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(ParseConstants.okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//ClientBuilder.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name", originalArtistName)
            return
        }

        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                apiKey = ParseConstants.API_KEY,
                artistName = convertArtistNameToQueryFormat(originalArtistName)
            )

            response.fold(
                { error ->
                    findArtistsUiState = FindArtistUiState.Error(error.toStringType(), originalArtistName)
                },
                { moshiResponse ->
                    val artistUIValues = moshiResponse.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(artistUIValues, originalArtistName)
                }
            )
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}

