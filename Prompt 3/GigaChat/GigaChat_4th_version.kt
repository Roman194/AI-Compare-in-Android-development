/**
* Код не запускается. Обрати внимание на findArtistsUiState во ViewModel. Согласно изначальному заданию эта переменная должна быть типа FindArtistUiState, 
* а не MutableState<FindArtistUiState>. Перепиши код так, чтобы эта переменная была заявленного в задании типа.
*/

//ArtistsApiService.kt
class ArtistsApiService {//GigaChat prompt 3 4th attempt

    private val artistsRetrofitService: ArtistsApiServiceInterface

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()

        artistsRetrofitService = retrofit.create(ArtistsApiServiceInterface::class.java)
    }

    suspend fun getSimilarArtists(artistName: String): Either<ErrorResponse, MoshiResponse> {
        return artistsRetrofitService.getSimilarArtists(
            apiKey = ParseConstants.API_KEY,
            artistName = artistName,
            mediaType = "music"
        )
    }
}

internal interface ArtistsApiServiceInterface {
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String,
        @Query("q") artistName: String,
        @Query("type") mediaType: String
    ): Either<ErrorResponse, MoshiResponse>
}

//ParseConstants.kt
class ParseConstants {
    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
    }
}

// FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState = FindArtistUiState.Start

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name.", "")
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        val formattedName = convertArtistNameToQueryFormat(originalArtistName)

        CoroutineScope(Dispatchers.IO).launch {
            val result = ArtistsApiService().getSimilarArtists(formattedName)

            withContext(Dispatchers.Main) {
                processAPIResponse(result, originalArtistName)
            }
        }
    }

    private fun processAPIResponse(response: Either<ErrorResponse, MoshiResponse>, originalArtistName: String) {
        findArtistsUiState = when (response) {
            is Either.Success -> {
                val artistList = response.value.similar.results.map { it.toUI() }
                FindArtistUiState.Success(artistList, originalArtistName)
            }
            is Either.Fail -> {
                FindArtistUiState.Error(response.value.toStringType(), originalArtistName)
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}


