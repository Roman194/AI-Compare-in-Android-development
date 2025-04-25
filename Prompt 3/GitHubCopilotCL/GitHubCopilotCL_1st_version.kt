//ArtistApiService.kt
interface ArtistsApiService {//GitHub Copilot CL 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(ParseConstants.client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            ))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()
    }

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//ParseConstants.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
        
        val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        }
    }
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {
    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            if (originalArtistName.isBlank()) {
                findArtistsUiState = FindArtistUiState.Error(
                    "Please enter an artist name",
                    originalArtistName
                )
                return@launch
            }

            findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
            
            try {
                val formattedArtistName = convertArtistNameToQueryFormat(originalArtistName)
                val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                    artistName = formattedArtistName
                )
                
                when (response) {
                    is Either.Success -> {
                        findArtistsUiState = FindArtistUiState.Success(
                            artistUIValues = response.data.toUI(),
                            originalArtist = originalArtistName
                        )
                    }
                    is Either.Fail -> {
                        findArtistsUiState = FindArtistUiState.Error(
                            errorMessage = response.error.toStringType(),
                            originalArtist = originalArtistName
                        )
                    }
                }
            } catch (e: Exception) {
                findArtistsUiState = FindArtistUiState.Error(
                    errorMessage = e.message ?: "Unknown error",
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}

