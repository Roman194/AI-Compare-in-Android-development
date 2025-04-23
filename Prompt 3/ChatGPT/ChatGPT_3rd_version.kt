/**
* Необходимо ещё немного доработать решение. Верни пожалуйста в ArtistsApiService.kt в функцию getSimilarArtists query k и передай в неё напрямую значение 
* ключа API из ParseConstants. 
*/

interface ArtistsApiService {//ChatGPT prompt 3 3rd attempt
    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") apiKey: String = ParseConstants.API_KEY,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}
