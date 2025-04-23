// 1. ClientBuilder.kt (Data Layer)
object ClientBuilder {//Gemini prompt 3 1st attempt

    object ParseConstants {
        const val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

// 2. ArtistsApiService.kt (Data Layer)
interface ArtistsApiService {
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ClientBuilder.ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    val artistsRetrofitService: ArtistsApiService by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .baseUrl("https://tastedive.com/api/")
            .client(ClientBuilder.okHttpClient)
            .build()

        retrofit.create(ArtistsApiService::class.java)
    }
}

// 3. FindArtistsViewModel. kt (UI Layer)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FindArtistUiState {
    data object Start : FindArtistUiState
    data object Loading : FindArtistUiState
    data class Success(val artistUIValues: List<ArtistUI>) : FindArtistUiState
    data class Error(val errorMessage: String) : FindArtistUiState
}

data class ArtistUI(
    val name: String = "",
    val type: String = ""
)

data class MoshiResponse(
    val Similar: MoshiResponseData
)

data class MoshiResponseData(
    val Results: List<ArtistMoshi>
)

data class ArtistMoshi(
    val Name: String,
    val Type: String
) {
    fun toUI(): ArtistUI {
        return ArtistUI(name = Name, type = Type)
    }
}

data class ErrorResponse(val message: String) {
    fun toStringType(): String {
        return message
    }
}

fun convertArtistNameToQueryFormat(artistName: String): String {
    return artistName.trim().replace(" ", "+")
}

class FindArtistsViewModel : ViewModel() {

    private val _findArtistsUiState = MutableStateFlow<FindArtistUiState>(FindArtistUiState.Start)
    val findArtistsUiState: StateFlow<FindArtistUiState> = _findArtistsUiState.asStateFlow()

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            _findArtistsUiState.value = FindArtistUiState.Loading
            if (originalArtistName.isBlank()) {
                _findArtistsUiState.value =
                    FindArtistUiState.Error("Please enter an artist name")
                return@launch
            }
            val queryArtistName = convertArtistNameToQueryFormat(originalArtistName)
            val result = ArtistApi.artistsRetrofitService.getSimilarArtists(artistName = queryArtistName)
            when (result) {
                is Either.Fail -> {
                    _findArtistsUiState.value =
                        FindArtistUiState.Error(result.value.toStringType())
                }

                is Either.Success -> {
                    val artists = result.value.Similar.Results.map { it.toUI() }
                    _findArtistsUiState.value = FindArtistUiState.Success(artists)
                }
            }
        }
    }
}

sealed class Either<out E, out V> {
    data class Fail<out E>(val value: E) : Either<E, Nothing>()
    data class Success<out V>(val value: V) : Either<Nothing, V>()
}
