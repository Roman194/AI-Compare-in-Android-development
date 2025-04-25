// !The running version of the code!
/**
* Необходимо доработать решение. В Moshi.Builder() не добавлен KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin 
* Object.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query "k", а не передавать его из ViewModel.

* Во ViewModel try-catch конструкция является лишней, так как кастомный Either уже обрабатывает эту ошибку и если он вдруг возникнет, то просто вызовется 
* ветка Either.Fail. Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только 
* тогда уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную 
* findArtistUiState можно вынести до оператора when.
*/

// ArtistsApiService.kt
interface ArtistsApiService { //Perplexity AI prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("q") originalArtistName: String,
        @Query("type") type: String = "music",
        @Query("k") apiKey: String = ParseConstants.API_KEY  // API ключ подставляется здесь автоматически
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())  // Добавлен KotlinJsonAdapterFactory
        .build()

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

//ClientBuilder.kt
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

//FindArtistsViewModel.kt
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
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(originalArtistName = queryName)

            findArtistsUiState = when (response) {
                is Either.Fail -> {
                    val errorMessage = response.value.toStringType()
                    FindArtistUiState.Error(
                        errorMessage = errorMessage,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Success -> {
                    val artistsList = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
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