// !The running version of the code!

/**
* Необходимо ещё немного доработать это решение. Во ViewModel в качестве параметра when должно быть просто response. Обработка response.value должна 
* производиться уже в Either.Fail-> ... или Either.Success-> ... (в зависимости от исхода).

* Также прошу не выводить в решении импорты.
*/

//FindArtistsViewModel.kt
package com.example.find_similar_artists_app.ui

class FindArtistsViewModel : ViewModel() { //GitHub Copilot GPT prompt 3 3rd attempt

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
        if (originalArtistName.trim().isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name", originalArtistName)
            return
        }
        viewModelScope.launch {
            val queryFormatted = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.getSimilarArtists(queryFormatted)
            when (response) {
                is Either.Fail -> {
                    findArtistsUiState =
                        FindArtistUiState.Error(response.value.toStringType(), originalArtistName)
                }
                is Either.Success -> {
                    val artistUIList = response.value.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(artistUIList, originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
