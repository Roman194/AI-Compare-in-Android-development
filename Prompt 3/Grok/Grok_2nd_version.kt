/**
* Необходимо доработать решение. Необходимо перенести object ArtistApi в файл ArtistsApiService.kt. okHttpClient хоть и находится в одном файле с классом 
* ParseConstants, но всё же должен быть объявлен за его пределами. Ещё HttpLoggingInterceptor() желательно предварительно объявить в качестве отдельной 
* переменной и только потом уже добавлять его в okHttpClient. В Moshi.Builder() не добавлен KotlinJsonAdapterFactory. Без этого десереализатор не настроен 
* на преобразование JSON в Kotlin Object.

* Также в ArtsistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query "k", а не передавать его из ViewModel.

* Про ViewModel. Обрати пожалуйста внимание, что чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только тогда
* уже конвертировать объект типа ErrorResponse или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную findArtistUiState 
* можно вынести до оператора when.
*/

//ArtistApiService.kt
	interface ArtistsApiService {//Grok prompt 3 2nd attempt
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
	    
	    private val retrofit = Retrofit.Builder()
	        .baseUrl("https://tastedive.com/api/")
	        .client(ParseConstants.okHttpClient)
	        .addConverterFactory(ScalarsConverterFactory.create())
	        .addConverterFactory(MoshiConverterFactory.create(moshi))
	        .addCallAdapterFactory(EitherCallAdapterFactory())
	        .build()
	    
	    val artistsRetrofitService: ArtistsApiService by lazy {
		        retrofit.create(ArtistsApiService::class.java)
	    }
	}
	val loggingInterceptor = HttpLoggingInterceptor().apply {
	    level = HttpLoggingInterceptor.Level.BODY
	}
	
	val okHttpClient = OkHttpClient.Builder()
	    .addInterceptor(loggingInterceptor)
	    .build()
	
//ParseConstants.kt
	class ParseConstants {
	    companion object {
	        val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
	    }
	}
//FindArtistsViewModel.kt
	class FindArtistsViewModel : ViewModel() {
	
	    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
	        private set
	
	    fun getSimilarArtists(originalArtistName: String) {
	        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
	        
	        if (originalArtistName.isBlank()) {
	            findArtistsUiState = FindArtistUiState.Error(
	                errorMessage = "Please enter an artist name",
	                originalArtist = originalArtistName
	            )
	            return
	        }
	
	        viewModelScope.launch {
	            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
	                artistName = convertArtistNameToQueryFormat(originalArtistName)
	            )
	            
	            findArtistsUiState = when (response) {
	                is Either.Fail -> FindArtistUiState.Error(
	                    errorMessage = response.value.toStringType(),
	                    originalArtist = originalArtistName
	                )
	                is Either.Success -> FindArtistUiState.Success(
	                    artistUIValues = response.value.similar.results.map { it.toUI() },
	                    originalArtist = originalArtistName
	                )
	            }
	        }
	    }
	
	    private fun convertArtistNameToQueryFormat(artistName: String): String {
	        return artistName.trim().replace(" ", "+")
	    }
	}


