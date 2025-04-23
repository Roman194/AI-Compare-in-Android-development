/**
* Необходимо ещё немного доработать решение. Верни пожалуйста в ArtistsApiService.kt в функцию getSimilarArtists query k и передай в неё напрямую значение 
* ключа API из ParseConstants. 

* Так как теперь в ArtistsApiService.kt используется переменная okHttpClient, инициализированная в этом файле, то в ClientBuilder.kt необходимо удалить 
* одноимённую переменную, так как она больше нигде не используется.

* Про ViewModel. Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только 
* тогда уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную 
* findArtistUiState можно вынести до оператора when. Тогда нужно внести переменную artistUIValues внутрь FindArtistUiState.Success. 
*/

//ArtistsApiService.kt
interface ArtistsApiService { //Mistral AI prompt 3 3rd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//ClientBuilder.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name", originalArtistName)
            return
        }

        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = convertArtistNameToQueryFormat(originalArtistName)
            )

            findArtistsUiState = when (response) {
                is Either.Success -> {
                    val artistUIValues = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(artistUIValues, originalArtistName)
                }
                is Either.Fail -> {
                    FindArtistUiState.Error(response.value.toStringType(), originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
