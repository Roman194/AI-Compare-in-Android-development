/**
* Необходимо доработать решение. В ClientBuilder HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной переменной и только потом 
* уже добавлять его в okHttpClient. Аналогично нужно поступить и в ArtistsApi с объявлением Moshi.Builder() до его добавления в Retrofit.Builder(). Также в 
* Moshi.Builder() не добавлен KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin Object. Также в билдере 
* EitherCallAdapterFactory должен иметь скобки () после себя. Ещё ParseConstants должен быть классом, а не объектом.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query k, а не передавать его из ViewModel.

* Про ViewModel. Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только тогда
* уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную findArtistUiState
* можно вынести до оператора when.
*/

//ArtistsApiService.kt
interface ArtistsApiService {//YandexGPT prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>

    companion object {
        const val API_KEY = ParseConstants.API_KEY
    }
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
