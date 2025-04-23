/**
*Отлично, код запускается! Но надо кое-что доработать. HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной переменной и только 
* потом уже добавлять в okHttpClient. 

* Во ViewModel try-catch конструкция является лишней, так как кастомный Either уже обрабатывает эту ошибку и если он вдруг возникнет, то просто вызовется 
* ветка Either.Fail. Также обрати внимание на то, что переменную findArtistUiState можно вынести до оператора when.
*/

object ClientBuilder {//Claude AI prompt 3 2nd attempt
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()
}

class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
}
class FindArtistsViewModel: ViewModel() {

    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)

    fun getSimilarArtists(originalArtistName: String) {
        // Set loading state first
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
        
        // Check if artist name is not empty
        if (originalArtistName.isBlank()) {
            findArtistsUiState = FindArtistUiState.Error(
                errorMessage = "Please enter an artist name", 
                originalArtist = originalArtistName
            )
            return
        }
        
        // Format artist name for query
        val formattedArtistName = convertArtistNameToQueryFormat(originalArtistName)
        
        // Launch coroutine for network request
        viewModelScope.launch {
            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                artistName = formattedArtistName
            )
            
            // Process the response based on Either type
            findArtistsUiState = when (response) {
                is Either.Success -> {
                    // Convert MoshiResponse to List<ArtistUI>
                    val artistList = response.value.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(
                        artistUIValues = artistList,
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


