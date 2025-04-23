// 1. ParseConstants.kt
object ParseConstants { //ChatGPT prompt 3 1st attempt
    const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
}
// 2. ClientBuilder.kt

object ClientBuilder {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

//3. ArtistsApiService.kt
interface ArtistsApiService {
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

//4. ArtistApi.kt
object ArtistApi {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(ClientBuilder.httpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

// FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        viewModelScope.launch {
            val query = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                apiKey = ParseConstants.API_KEY,
                artistName = query
            )

            findArtistsUiState = when (response) {
                is Either.Success -> {
                    val artists = response.data.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(artistUIValues = artists, originalArtist = originalArtistName)
                }
                is Either.Fail -> {
                    val errorMsg = response.error.toStringType()
                    FindArtistUiState.Error(errorMessage = errorMsg, originalArtist = originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}

