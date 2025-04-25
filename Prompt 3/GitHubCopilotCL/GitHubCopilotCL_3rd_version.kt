// !The running version of the code!

/**
* Необходимо ещё немного доработать решение. Дело в том, что переменная okHttpClient используется в другом файле и поэтому не может иметь модификатор private.
* Его необходимо убрать.  Также обрати внимание на when во ViewModel. Он должен получать в качестве параметра переменную response, а response.value должно 
* вызываться уже после определения случая (Fail или Success).

* После response.value должна начинаться работа с объектом типа MoshiResponse или ErrorResponse (зависит от исхода), но между ними нет никаких промежуточных 
* этапов с переменными error или data.
*/

private val loggingInterceptor = HttpLoggingInterceptor().apply {//GitHub Copilot CL 3rd attempt
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}

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

            findArtistsUiState = when (response) {
                is Either.Success -> FindArtistUiState.Success(
                    artistUIValues = response.value.similar.results.map { it.toUI() },
                    originalArtist = originalArtistName
                )
                is Either.Fail -> FindArtistUiState.Error(
                    errorMessage = response.value.toStringType(),
                    originalArtist = originalArtistName
                )
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
