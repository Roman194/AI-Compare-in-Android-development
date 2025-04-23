/**
* Необходимо модифицировать решение. okHttpClient хоть и находится в одном файле с классом ParseConstants, но всё же должен быть объявлен за его пределами. 
* Ещё HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной переменной и только потом уже добавлять его в okHttpClient. 
* Так как okHttpClient объявлен за пределами какого-либо класса, то в Retrofit.Builder() его нужно вызывать только по названию переменной. Moshi.Builder() 
* до его добавления в Retrofit.Builder() должен быть объявлен и инициализирован до Retrofit.Builder(). Также в Moshi.Builder() не добавлен 
* KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin Object.
 
* В ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query "k" из ParseConstants, а не передавать его из 
* ViewModel.

* Во ViewModel  в when нужно лишь вызывать параметр result, а его объявление и инициализация должно происходить ранее. В Either.Fail после result.value 
* должна начинаться работа с объектом типа ErrorResponse и между ними нет никаких промежуточных этапов с переменными error или data. Также обрати внимание 
* на то, что переменную findArtistUiState можно вынести до оператора when.
*/

// Файл: ArtistsApiService.kt
interface ArtistsApiService {//DeepSeek prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

object ArtistApi {
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

// Файл: ClientBuilder.kt
class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

// Файл: FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {
    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
            
            if (originalArtistName.isBlank()) {
                findArtistsUiState = FindArtistUiState.Error(
                    errorMessage = "Please enter an artist name",
                    originalArtist = originalArtistName
                )
                return@launch
            }

            val queryName = convertArtistNameToQueryFormat(originalArtistName)
            val result = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = queryName
            )
            
            findArtistsUiState = when (result) {
                is Either.Success -> {
                    val artists = result.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
                        artistUIValues = artists,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Fail -> {
                    FindArtistUiState.Error(
                        errorMessage = result.value.toStringType(),
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
