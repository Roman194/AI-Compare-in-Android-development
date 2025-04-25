
//ArtistApiService.kt
interface ArtistsApiService { //Perplexity AI prompt 3 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") originalArtistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val moshi = Moshi.Builder().build()

    private val okHttpClient = ClientBuilder.okHttpClient

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService = retrofit.create(ArtistsApiService::class.java)
}

// ClientBuilder.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

object ClientBuilder {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

// FindArtistsViewModel.kt (добавлена реализация getSimilarArtists)
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        val trimmedName = originalArtistName.trim()
        if (trimmedName.isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtist = originalArtistName)

        viewModelScope.launch {
            val queryName = convertArtistNameToQueryFormat(trimmedName)
            val response = try {
                ArtistApi.artistsRetrofitService.getSimilarArtists(
                    apiKey = ParseConstants.API_KEY,
                    originalArtistName = queryName
                )
            } catch (e: Exception) {
                // В случае исключения считаем это сетевой ошибкой
                Either.Fail(ErrorResponse.getValue(700))
            }

            when (response) {
                is Either.Fail -> {
                    val errorMessage = response.error.toStringType()
                    findArtistsUiState = FindArtistUiState.Error(
                        errorMessage = errorMessage,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Success -> {
                    val artistsList = response.value.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(
                        artistUIValues = artistsList,
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


