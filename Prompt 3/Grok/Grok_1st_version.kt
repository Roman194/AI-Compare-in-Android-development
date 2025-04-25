
//ArtistApiService.kt
interface ArtistsApiService {//Grok prompt 3 1th attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val moshi = Moshi.Builder().build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(ParseConstants.okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()
    
    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//ParseConstants.kt
class ParseConstants {
    companion object {
        val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
        
        val okHttpClient = OkHttpClient.Builder()
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
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                apiKey = ParseConstants.API_KEY,
                artistName = convertArtistNameToQueryFormat(originalArtistName)
            )
            
            findArtistsUiState = when (response) {
                is Either.Fail -> FindArtistUiState.Error(
                    errorMessage = response.error.toStringType(),
                    originalArtist = originalArtistName
                )
                is Either.Success -> FindArtistUiState.Success(
                    artistUIValues = response.value.similar.results.map { it.toUI() },
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}


