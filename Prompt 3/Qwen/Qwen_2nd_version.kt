/**
* Необходимо доработать решение.  Необходимо перенести object ArtistApi в файл ArtistsApiService.kt. В переменную moshi после Moshi.Builder() не добавлен 
* KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin Object.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую, а не передавать его из ViewModel.

* Во ViewModel try-catch конструкция является лишней, так как кастомный Either уже обрабатывает эту ошибку и если он вдруг возникнет, то просто вызовется 
* ветка Either.Fail. Также обрати внимание на то, что переменную findArtistUiState можно вынести до оператора when.
*/

//1. ArtistsApiService.kt

interface ArtistApiService { //Qwen prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
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

//2. ClientBuilder.kt
class ParseConstants {
    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
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
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(queryArtistName)

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


