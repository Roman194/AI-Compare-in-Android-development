/**
* Необходимо модифицировать решение.

* В ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query "k" из ParseConstants, а не передавать его из 
* ViewModel.

* Во ViewModel после response.value должна начинаться работа с объектом типа MoshiResponse или ErrorResponse (зависит от исхода), но между ними нет никаких 
* промежуточных этапов с переменными error или data.
*/

// 1. ArtistsApiService.kt
interface ArtistsApiService { //ChatGPT prompt 3 2nd attempt

    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

// FindArtistsViewModel.kt
class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
        private set

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
            val query = convertArtistNameToQueryFormat(originalArtistName)
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(artistName = query)

            findArtistsUiState = when (response) {
                is Either.Success -> {
                    val artists = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(artistUIValues = artists, originalArtist = originalArtistName)
                }
                is Either.Fail -> {
                    val errorMsg = response.value.toStringType()
                    FindArtistUiState.Error(errorMessage = errorMsg, originalArtist = originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}

