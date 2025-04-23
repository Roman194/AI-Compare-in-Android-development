/**
* Необходимо ещё доработать решение. Верни пожалуйста в ArtistsApiService.kt в функцию getSimilarArtists query k и передай в неё напрямую значение ключа API 
* из ParseConstants. Companion object в этом интерфейсе необходимо удалить.

* Про ViewModel. В FindArtistUiState.Error нужно передать response.value.toStringType()
*/

//ArtistsApiService.kt
interface ArtistsApiService {//YandexGPT prompt 3 3rd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(ClientBuilder.okHttpClient)
        .baseUrl("https://tastedive.com/api/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService = retrofit.create(ArtistsApiService::class.java)
}

//ClientBuilder.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

object ClientBuilder {
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {
    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name", originalArtistName)
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                ParseConstants.API_KEY,
                convertArtistNameToQueryFormat(originalArtistName)
            )

            val result = when (response) {
                is Either.Success -> {
                    val artistUIValues = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(artistUIValues, originalArtistName)
                }
                is Either.Fail -> {
                    FindArtistUiState.Error(response.error.toStringType(), originalArtistName)
                }
            }

            findArtistsUiState = result
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}

