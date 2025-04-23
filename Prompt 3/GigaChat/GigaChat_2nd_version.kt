/**
* Необходимо доработать решение. Объект ArtistApi необходимо перенести в файл  ArtistsApiService. HttpLoggingInterceptor() желательно предварительно объявить
* в качестве отдельной переменной и только потом уже добавлять его в okHttpClient. В Moshi.Builder() не добавлен KotlinJsonAdapterFactory. Без этого 
* десереализатор не настроен на преобразование JSON в Kotlin Object. ParseConstants должен быть классом, а не объектом.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в qurey "k", а не передавать его из ViewModel. 
* В интерфейсе ArtistsApiService companion object является излишним: достаточно просто приравнять query "type" ко "music" в функции getSimilarArtists.

* Насчёт FindArtistsViewModel. Either не имеет метода Failure, только метод Fail. Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только тогда уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную findArtistUiState можно вынести до оператора when.
*/
//ArtistsApiService.kt
class ArtistsApiService {//GigaChat prompt 3 2nd attempt

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
            .addCallAdapterFactory(EitherCallAdapterFactory.create())
            .build()

        artistsRetrofitService = retrofit.create(ArtistsApiServiceInterface::class.java)
    }

    suspend fun getSimilarArtists(artistName: String): Either<ErrorResponse, MoshiResponse> {
        return try {
            val response = artistsRetrofitService.getSimilarArtists(apiKey = BuildConfig.TASTE_DIVE_API_KEY, artistName = artistName, mediaType = "music")
            response.value
        } catch (e: Exception) {
            Either.fail(ErrorResponse(-1, "Unexpected exception: ${e.message}"))
        }
    }

    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
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

