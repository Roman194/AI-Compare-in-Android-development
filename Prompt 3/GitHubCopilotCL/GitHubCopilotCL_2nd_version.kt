/**
*Модифицируй это решение. 
* okHttpClient хоть и находится в одном файле с классом ParseConstants, но всё же должен быть объявлен за его пределами. Ещё HttpLoggingInterceptor() 
* желательно предварительно объявить в качестве отдельной переменной и только потом уже добавлять его в okHttpClient. Аналогично нужно поступить и с 
* объявлением Moshi.Builder() до его добавления в Retrofit.Builder().  Во ViewModel try-catch конструкция является лишней, так как кастомный Either уже 
* обрабатывает эту ошибку и если он вдруг возникнет, то просто вызовется ветка Either.Fail. Также обрати внимание на то, что переменную findArtistUiState 
* можно вынести до оператора when.  Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как 
* response.value и только тогда уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState.
*/

private val loggingInterceptor = HttpLoggingInterceptor().apply {//GitHub Copilot CL 2nd attempt
    level = HttpLoggingInterceptor.Level.BODY
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

//ArtistsApiService.kt:
interface ArtistsApiService {
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://tastedive.com/api/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()
    }

    val artistsRetrofitService: ArtistsApiService by lazy {
        retrofit.create(ArtistsApiService::class.java)
    }
}

//FindArtistsViewModel.kt:
class FindArtistsViewModel : ViewModel() {
    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            if (originalArtistName.isBlank()) {
                findArtistsUiState = FindArtistUiState.Error(
                    "Please enter an artist name",
                    originalArtistName
                )
                return@launch
            }

            findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
            
            val formattedArtistName = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = formattedArtistName
            )

            findArtistsUiState = when (val result = response.value) {
                is Either.Success -> FindArtistUiState.Success(
                    artistUIValues = result.data.similar.results.map { it.toUI() },
                    originalArtist = originalArtistName
                )
                is Either.Fail -> FindArtistUiState.Error(
                    errorMessage = result.error.toStringType(),
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}