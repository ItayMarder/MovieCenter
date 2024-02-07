package com.app.moviecenter.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

@Serializable
data class ApiResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("description") val description: List<String>
)


suspend fun getMovieId(movieName: String): String {
    try {
        val encodedName = java.net.URLEncoder.encode(movieName, "utf-8")
        val apiUrl = "https://search.imdbot.workers.dev/?q=$encodedName"

        val url: URL = URI.create(apiUrl).toURL()

        val responseCode: Int = withContext(Dispatchers.IO) {
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
            }.responseCode
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {

            val res = withContext(Dispatchers.IO) {
                (url.openConnection() as HttpURLConnection).inputStream.bufferedReader().readText()
            }

            val movieJSONObj = JSONObject(res).getJSONArray("description").getJSONObject(0)

            return movieJSONObj.getString("#IMDB_ID")

        } else {
            return ""
        }
    } catch (e: Exception) {
        return ""
    }
}

suspend fun getMovieRank(movieId: String): Double {
    try {
        val apiUrl = "https://search.imdbot.workers.dev/?tt=$movieId"

        val url: URL = URI.create(apiUrl).toURL()

        val responseCode: Int = withContext(Dispatchers.IO) {
            (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
            }.responseCode
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {

            val res = withContext(Dispatchers.IO) {
                (url.openConnection() as HttpURLConnection).inputStream.bufferedReader().readText()
            }

            return JSONObject(res).getJSONObject("short")
                .getJSONObject("aggregateRating").getDouble("ratingValue")

        }

        return 0.0
    } catch (e: Exception) {
        return 0.0
    }
}


