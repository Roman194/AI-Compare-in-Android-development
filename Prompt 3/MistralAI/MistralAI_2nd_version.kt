/**
* Необходимо доработать решение.  okHttpClient хоть и находится в одном файле с классом ParseConstants, но всё же должен быть объявлен за его пределами. 
* Ещё HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной переменной и только потом уже добавлять его в okHttpClient. 
* Аналогично нужно поступить и с объявлением Moshi.Builder() до его добавления в Retrofit.Builder(). Также в Moshi.Builder() не добавлен 
* KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin Object.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую, а не передавать его из ViewModel.

* Насчёт FindArtistsViewModel. Either предполагает использование оператора when по отношению к переменной response. При этом возможны два сценария: 
* is Either.Success -> ... и is Either.Fail -> ...
*/

//ArtistsApiService.kt
interface ArtistsApiService { //Mistral AI prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
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

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

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

            when (response) {
                is Either.Success -> {
                    val artistUIValues = response.value.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(artistUIValues, originalArtistName)
                }
                is Either.Fail -> {
                    findArtistsUiState = FindArtistUiState.Error(response.error.toStringType(), originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
