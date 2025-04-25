// !The running version of the code!
/**
* Возникает ошибка компиляции. Дело в том, что во ViewModel при вызове suspend fun getSimilarArtists не задано какой именно из трёх параметров задаётся 
* (не указано имя задаваемого параметра, а так как параметр apiKey идёт первым, то происходит попытка принять значение переменной queryArtistName именно в 
* него). Необходимо решить эту проблему.
*/

//1. ArtistsApiService.kt

interface ArtistApiService {//Qwen prompt 3 4th attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("q") artistName: String,
        @Query("type") type: String = "music",
        @Query("k") apiKey: String = ParseConstants.API_KEY // API ключ теперь последний и имеет дефолтное значение
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Настройка для работы с Kotlin объектами
            .build()

        Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()
    }

    val artistsRetrofitService: ArtistApiService by lazy {
        retrofit.create(ArtistApiService::class.java)
    }
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        // Initial state change to Loading
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        // Check if the artist name is empty
        if (originalArtistName.trim().isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        // Convert artist name to query format
        val queryArtistName = convertArtistNameToQueryFormat(originalArtistName)

        // Launch a coroutine to perform the network request
        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = queryArtistName // Передаем только имя артиста
            )

            // Обновляем состояние UI в зависимости от результата запроса
            findArtistsUiState = when (response) {
                is Either.Fail -> {
                    val error = response.value
                    FindArtistUiState.Error(
                        errorMessage = error.toStringType(),
                        originalArtist = originalArtistName
                    )
                }
                is Either.Success -> {
                    val similarArtists = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
                        artistUIValues = similarArtists,
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
