package edu.lemoyne.campusapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Header

interface ShowcaseApi {
    @GET( "/rest/v1/showcase_items")
    suspend fun getShowcaseItems(
            @Header("apikey") apiKey: String
    ): List<ShowcaseItem>
}

object ShowcaseService {
    const val API_KEY = "sb_publishable_YEzZ_Hv6JIJXvVfEZRZA8g_iP5K4Cpl"
    const val BASE_URL = "https://gtyzrglymdnwnejwsnem.supabase.co"

    val api: ShowcaseApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ShowcaseApi::class.java)
}