package edu.lemoyne.campusapp;

public class ShowcaseRepository(private val api: ShowcaseApi = ShowcaseService.api) {

    suspend fun loadItems(): List<ShowcaseItem> =
        withContext(Dispatchers.IO) {
            api.getShowcaseItems(ShowcaseService.API_KEY)
        }
}
