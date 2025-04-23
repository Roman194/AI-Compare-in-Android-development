package com.example.find_similar_artists_app.data

import com.example.find_similar_artists_app.models.data.ErrorResponse
import com.example.find_similar_artists_app.models.data.moshi.MoshiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistsApiService {//GitHub Copilot GPT prompt 3 1st attempt

    @GET("similar")
    suspend fun getSimilarArtists(
        @Query("k") key: String,
        @Query("q") artistName: String,
        @Query("type") type: String = "music"
    ): Either<ErrorResponse, MoshiResponse>
}

package com.example.find_similar_artists_app.data

import com.example.find_similar_artists_app.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ParseConstants {
    companion object {
        val API_KEY: String = BuildConfig.TASTE_DIVE_API_KEY
    }
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

    val artistsRetrofitService: ArtistsApiService =
        retrofit.create(ArtistsApiService::class.java)
}

package com.example.find_similar_artists_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.find_similar_artists_app.data.ArtistApi
import com.example.find_similar_artists_app.data.ParseConstants
import com.example.find_similar_artists_app.data.Either
import com.example.find_similar_artists_app.models.data.ErrorResponse
import com.example.find_similar_artists_app.models.data.moshi.toUI
import kotlinx.coroutines.launch

class FindArtistsViewModel : ViewModel() {

    var findArtistsUiState: FindArtistUiState = FindArtistUiState.Start

    fun getSimilarArtists(originalArtistName: String) {
        findArtistsUiState = FindArtistUiState.Loading(originalArtistName)
        if (originalArtistName.trim().isEmpty()) {
            findArtistsUiState = FindArtistUiState.Error("Please enter an artist name", originalArtistName)
            return
        }
        viewModelScope.launch {
            val queryFormatted = convertArtistNameToQueryFormat(originalArtistName)
            when (val response = ArtistApi.artistsRetrofitService.getSimilarArtists(
                key = ParseConstants.API_KEY,
                artistName = queryFormatted,
                type = "music"
            )) {
                is Either.Fail -> {
                    val errorMsg = response.error.toStringType()
                    findArtistsUiState = FindArtistUiState.Error(errorMsg, originalArtistName)
                }
                is Either.Success -> {
                    val artistUIList = response.data.similar.results.map { it.toUI() }
                    findArtistsUiState = FindArtistUiState.Success(artistUIList, originalArtistName)
                }
            }
        }
    }

    private fun convertArtistNameToQueryFormat(artistName: String): String {
        return artistName.trim().replace(" ", "+")
    }
}
