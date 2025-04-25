// !The running version of the code!
/**
* Необходимо доработать решение. HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной переменной и только потом уже добавлять его 
* в okHttpClient. В Moshi.Builder() не добавлен KotlinJsonAdapterFactory. Без этого десереализатор не настроен на преобразование JSON в Kotlin Object. Также в
* ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query "k", а не передавать его из ViewModel.
*/

//ArtistsApiService.kt
interface ArtistsApiService {//Microsoft 365 Copilot prompt 3 2nd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY, // Получение API-ключа напрямую
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private const val BASE_URL = "https://tastedive.com/api/"

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Добавлен KotlinJsonAdapterFactory
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(ClientBuilder.okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    val artistsRetrofitService: ArtistsApiService = retrofit.create(ArtistsApiService::class.java)
}

//ClientBuilder.kt
object ClientBuilder {
    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply { // HttpLoggingInterceptor объявлен отдельно
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor) // Добавлен HttpLoggingInterceptor
        .build()
}

class ParseConstants {
    companion object {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
    }
}

//FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name",
                originalArtist = originalArtistName
            )
            return
        }

        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)

        viewModelScope.launch {
            val artistQuery = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = artistQuery
            )

            findArtistsUiState = when (response) {
                is Either.Success -> {
                    val artistUIValues = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
                        artistUIValues = artistUIValues,
                        originalArtist = originalArtistName
                    )
                }
                is Either.Fail -> {
                    FindArtistUiState.Error(
                        errorMessage = response.value.toStringType(),
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


