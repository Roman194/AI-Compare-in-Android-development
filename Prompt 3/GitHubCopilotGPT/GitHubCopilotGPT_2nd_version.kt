/**
* Необходимо доработать решение. Необходимо перенести object ArtistApi в файл ArtistsApiService.kt.

* Также в ArtistApiService.kt в suspend-функции getSimilarArtists нужно получать значение API ключа напрямую в query k, а не передавать его из ViewModel.

* Про ViewModel. Переменную response необходимо объявить заранее и только тогда передать её в качестве параметра для when. Обрати пожалуйста внимание, что 
* чтобы получить результат от переменной response необходимо к ней обратиться как response.value и только тогда уже конвертировать объект типа ErrorResponse 
* или MoshiResponse в нужный в FindArtistUiState. Также обрати внимание на то, что переменную findArtistUiState можно вынести до оператора when.
*/

//ArtistApiService.kt
package com.example.find_similar_artists_app.data

import com.example.find_similar_artists_app.BuildConfig
import com.example.find_similar_artists_app.models.data.ErrorResponse
import com.example.find_similar_artists_app.models.data.moshi.MoshiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

interface ArtistsApiService {//GitHub Copilot GPT prompt 3 2nd attempt

    @GET("similar")
    suspend fun getSimilarArtistsInternal(
        @Query("k") key: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

object ArtistApi {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tastedive.com/api/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .build()

    private val retrofitService: ArtistsApiService = retrofit.create(ArtistsApiService::class.java)

    suspend fun getSimilarArtists(artistName: String): Either<ErrorResponse, MoshiResponse> {
        return retrofitService.getSimilarArtistsInternal(
            key = ParseConstants.API_KEY,
            artistName = artistName,
            type = "music"
        )
    }
}

//FindArtistsViewModel.kt
package com.example.find_similar_artists_app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.find_similar_artists_app.data.ArtistApi
import com.example.find_similar_artists_app.models.data.toStringType
import com.example.find_similar_artists_app.models.data.moshi.toUI
import kotlinx.coroutines.launch

class FindArtistsViewModel : ViewModel() {

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
            findArtistsUiState = when (val result = response.value) {
                is com.example.find_similar_artists_app.models.data.ErrorResponse ->
                    FindArtistUiState.Error(result.toStringType(), originalArtistName)
                is com.example.find_similar_artists_app.models.data.moshi.MoshiResponse -> {
                    val artistUIList = result.similar.results.map { it.toUI() }
                    FindArtistUiState.Success(artistUIList, originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
