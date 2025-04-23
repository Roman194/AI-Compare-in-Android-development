//ArtistApiServise.kt
interface ArtistsApiService {//Claude AI Prompt 3 1st attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit = ClientBuilder.retrofit

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//ClientBuilder.kt
object ClientBuilder {
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()
}

class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}
class FindArtistsViewModel: ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        // Set loading state first
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
        
        // Check if artist name is not empty
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name", 
                originalArtist = originalArtistName
            )
            return
        }
        
        // Format artist name for query
        val formattedArtistName = convertArtistNameToQueryFormat(originalArtistName)
        
        // Launch coroutine for network request
        viewModelScope.launch {
            try {
                // Make API call
                val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                    artistName = formattedArtistName
                )
                
                // Process the response based on Either type
                when (response) {
                    is Either.Success -> {
                        // Convert MoshiResponse to List<ArtistUI>
                        val artistList = response.value.similar.results.map { it.toUI() }
                        findArtistsUiState = FindArtistUiState.Success(
                            artistUIValues = artistList,
                            originalArtist = originalArtistName
                        )
                    }
                    is Either.Fail -> {
                        findArtistsUiState = FindArtistUiState.Error(
                            errorMessage = response.value.toStringType(),
                            originalArtist = originalArtistName
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle unexpected errors
                findArtistsUiState = FindArtistUiState.Error(
                    errorMessage = ErrorResponse(700, errorCause = e.message ?: "").toStringType(),
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}


