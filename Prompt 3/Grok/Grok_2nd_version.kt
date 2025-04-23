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
	interface ArtistsApiService {//Grok prompt 3 2nd attempt
	    @GET("similar")
	    suspend fun getSimilarArtists(
	        @Query("k") apiKey: String = ParseConstants.API_KEY,
            @Query("q") artistName: String,
	        @Query("type") type: String = "music"
	    ): Either<ErrorResponse, MoshiResponse>
	}
	
	object ArtistApi {
17.	    private val moshi = Moshi.Builder()
18.	        .add(KotlinJsonAdapterFactory())
19.	        .build()
20.	    
21.	    private val retrofit = Retrofit.Builder()
22.	        .baseUrl("https://tastedive.com/api/")
23.	        .client(ParseConstants.okHttpClient)
24.	        .addConverterFactory(ScalarsConverterFactory.create())
25.	        .addConverterFactory(MoshiConverterFactory.create(moshi))
26.	        .addCallAdapterFactory(EitherCallAdapterFactory())
27.	        .build()
28.	    
29.	    val artistsRetrofitService: ArtistsApiService by lazy {
30.	        retrofit.create(ArtistsApiService::class.java)
31.	    }
32.	}
33.	val loggingInterceptor = HttpLoggingInterceptor().apply {
34.	    level = HttpLoggingInterceptor.Level.BODY
35.	}
36.	
37.	val okHttpClient = OkHttpClient.Builder()
38.	    .addInterceptor(loggingInterceptor)
39.	    .build()
40.	
41.	class ParseConstants {
42.	    companion object {
43.	        val API_KEY = BuildConfig.TASTE_DIVE_API_KEY
44.	    }
45.	}
46.	class FindArtistsViewModel : ViewModel() {
47.	
48.	    var findArtistsUiState: FindArtistUiState by mutableStateOf(FindArtistUiState.Start)
49.	        private set
50.	
51.	    fun getSimilarArtists(originalArtistName: String) {
52.	        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
53.	        
54.	        if (originalArtistName.isBlank()) {
55.	            findArtistsUiState = FindArtistUiState.Error(
56.	                errorMessage = "Please enter an artist name",
57.	                originalArtist = originalArtistName
58.	            )
59.	            return
60.	        }
61.	
62.	        viewModelScope.launch {
63.	            val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
64.	                artistName = convertArtistNameToQueryFormat(originalArtistName)
65.	            )
66.	            
67.	            findArtistsUiState = when (response) {
68.	                is Either.Fail -> FindArtistUiState.Error(
69.	                    errorMessage = response.value.toStringType(),
70.	                    originalArtist = originalArtistName
71.	                )
72.	                is Either.Success -> FindArtistUiState.Success(
73.	                    artistUIValues = response.value.similar.results.map { it.toUI() },
74.	                    originalArtist = originalArtistName
75.	                )
76.	            }
77.	        }
78.	    }
79.	
80.	    private fun convertArtistNameToQueryFormat(artistName: String): String {
81.	        return artistName.trim().replace(" ", "+")
82.	    }
83.	}


