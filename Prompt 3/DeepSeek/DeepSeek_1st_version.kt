// Файл: ArtistsApiService.kt
interface ArtistsApiService { //DeepSeek prompt 3 1st attempt
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
        .client(ClientBuilder.okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(
            Moshi.Builder().build()
        ))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

// Файл: ClientBuilder.kt
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

// Файл: FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {
    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
            
            if (originalArtistName.isBlank()) {
                findArtistsUiState = FindArtistUiState.Error(
                    errorMessage = "Please enter an artist name",
                    originalArtist = originalArtistName
                )
                return@launch
            }

            val queryName = convertArtistNameToQueryFormat(originalArtistName)
            
            when (val result = ArtistApi.artistsRetrofitService.getSimilarArtists(
                apiKey = ParseConstants.API_KEY,
                artistName = queryName
            )) {
                is Either.Success -> {
                    val artists = result.value.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(
                        artistUIValues = artists,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Fail -> {
                    findArtistsUiState = FindArtistUiState.Error(
                        errorMessage = result.error.toStringType(),
                        originalArtist = originalArtistName
                    )
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}


