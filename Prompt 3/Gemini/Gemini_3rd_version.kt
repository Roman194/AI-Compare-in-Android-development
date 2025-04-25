// !The running version of the code!
/**
* Необходимо ещё доработать решение. findArtistsUiState должно инициализироваться следующим образом: var findArtistsUiState: FindArtistUiState by 
* mutableStateOf(FindArtistUiState.Start). Ниже прикрепляю FindArtistUiState и используемые в проекте data-классы. Необходимо переписать ViewModel 
* под использование этих данных.
*/

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.find_similar_artists_app.data.ArtistApi
import com.example.find_similar_artists_app.data.Either
import com.example.find_similar_artists_app.models.ArtistUI
import com.example.find_similar_artists_app.models.data.ArtistMoshi
import com.example.find_similar_artists_app.models.data.ErrorResponse
import com.example.find_similar_artists_app.models.data.MoshiResponse
import com.example.find_similar_artists_app.models.data.toUI
import com.example.find_similar_artists_app.models.data.toStringType
import kotlinx.coroutines.launch

sealed interface FindArtistUiState { //Gemini prompt 3 3rd attempt
    data class Success(val artistUIValues: List<ArtistUI>, val originalArtist: String) :
        FindArtistUiState

    data class Error(val errorMessage: String, val originalArtist: String) : FindArtistUiState
    data class Loading(val originalArtist: String) : FindArtistUiState
    object Start : FindArtistUiState
}

fun convertArtistNameToQueryFormat(artistName: String): String {
    return artistName.trim().replace(" ", "+")
}

class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

    fun getSimilarArtists(originalArtistName: String) {
        viewModelScope.launch {
            if (originalArtistName.isBlank()) {
                findArtistsUiState =
                    FindArtistUiState.Error("Please enter an artist name", originalArtistName)
                return@launch
            }
            findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
            val queryArtistName = convertArtistNameToQueryFormat(originalArtistName)
            val result =
                ArtistApi.artistsRetrofitService.getSimilarArtists(artistName = queryArtistName)
            when (result) {
                is Either.Fail -> {
                    findArtistsUiState =
                        FindArtistUiState.Error(result.value.toStringType(), originalArtistName)
                }

                is Either.Success -> {
                    val artists = result.value.similar.results.map { it.toUI() }
                    findArtistsUiState =
                        FindArtistUiState.Success(artists, originalArtistName)
                }
            }
        }
    }
}

