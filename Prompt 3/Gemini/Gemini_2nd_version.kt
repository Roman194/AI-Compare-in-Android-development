/**
*Необходимо дорботать решение. Обрати внимание на findArtistsUiState во ViewModel. Согласно изначальному заданию эта переменная должна быть типа 
* FindArtistUiState, а не MutableState<FindArtistUiState>. Перепиши код так, чтобы эта переменная была заявленного в задании типа. 
* Также в целом обрати внимание на sealed interface FindArtistUiState и какие параметры он принимает при различных состояниях.

* Насчёт перечисленных выше data-классов. По заданию не требовалось их создание, так как они уже созданы в проекте. Посмотри пожалуйста какие переменные в 
* них входят и используй это при десереализации пришедших данных из интернета во ViewModel.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.find_similar_artists_app.data.ArtistApi
import com.example.find_similar_artists_app.data.Either.Either
import com.example.find_similar_artists_app.models.data.ArtistMoshi
import com.example.find_similar_artists_app.models.data.ErrorResponse
import com.example.find_similar_artists_app.models.data.MoshiResponse
import com.example.find_similar_artists_app.models.data.toStringType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FindArtistUiState {//Gemini prompt 3 2nd attempt
    data object Start : FindArtistUiState
    data object Loading : FindArtistUiState
    data class Success(val artistUIValues: List<ArtistUI>) : FindArtistUiState
    data class Error(val errorMessage: String) : FindArtistUiState
}

data class ArtistUI(
    val name: String = "",
    val type: String = ""
)

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


