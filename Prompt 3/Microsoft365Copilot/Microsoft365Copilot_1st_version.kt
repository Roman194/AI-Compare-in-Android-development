//ArtistsApiService.kt
interface ArtistsApiService {//Microsoft 365 Copilot prompt 3 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private const val BASE_URL = "https://tastedive.com/api/"

    private val moshi: Moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(ClientBuilder.okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService = retrofit.create(ArtistsApiService::class.java)
}

//ClientBuilder.kt
object ClientBuilder {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
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
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        viewModelScope.launch {
            val artistQuery = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                apiKey = ParseConstants.API_KEY,
                artistName = artistQuery
            )

            findArtistsUiState = when (response) {
                is Either.Success -> {
                    val artistUIValues = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
                        artistUIValues = artistUIValues,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Fail -> {
                    FindArtistUiState.Error(
                        errorMessage = response.value.toStringType(),
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
