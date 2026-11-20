package edu.lemoyne.campusapp;

public class ShowcaseRepository(private val api: ShowcaseApi = ShowcaseService.api) {

    suspend fun loadItems(): List<ShowcaseItem> =
        withContext(Dispatchers.IO) {
        api.getShowcaseItems(ShowcaseService.API_KEY, "Bearer $token")
        }
        suspend fun signIN(email: String, password: String): String =
        withContext(Dispatchers.IO) {
        api.signIn(
               apiKey = ShowacseService.API_KEY,
               request = LoginRequest(email, password)
        ).accessToken


        }
}
