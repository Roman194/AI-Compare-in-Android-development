/**
* Необходимо ещё доработать решение. В классе ArtistsApiService при инициализации Retrofit.Builder() необходимо убрать метод create у EitherCallAdapterFactory
* так как он лишний, но оставить круглые скобки. В функции getSimilarArtists не нужно использовать переменную response. Вместо этого нужно возвращать значение
* функции artistsRetrofitService.getSimilarArtists. try-catch конструкция тоже является лишней, так как кастомный Either уже обрабатывает эту ошибку и если он
* вдруг возникнет, то просто вызовется ветка Either.Fail. 

* Также companion object в этом классе является лишним и его нужно удалить. В качестве параметра apiKey в artistsRetrofitService.getSimilarArtists нужно 
* передавать ParseConstants.API_KEY.
*/

//ArtistsApiService.kt
class ArtistsApiService {//GigaChat prompt 3 3rd attempt

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

// ParseConstants.kt
class ParseConstants {
    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
    }
}

// FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: MutableState<FindArtistUiState> = mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isBlank()) {
            findArtistsUiState.value = FindArtistUiState.Error("Please enter an artist name.", "")
            return
        }

        findArtistsUiState.value = FindArtistUiState.Loading(originalArtistName)

        val formattedName = convertArtistNameToQueryFormat(originalArtistName)

        CoroutineScope(Dispatchers.IO).launch {
            val result = ArtistsApiService().getSimilarArtists(formattedName)

            withContext(Dispatchers.Main) {
                processAPIResponse(result, originalArtistName)
            }
        }
    }

    private fun processAPIResponse(response: Either<ErrorResponse, MoshiResponse>, originalArtistName: String) {
        findArtistsUiState.value = when (response) {
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

